/*************************************
*
*   우쿨렐레 연주 데이터 만들어 주는 변환기.
*      엑셀파일로 TAB악보를 편집, CSV 형식으로 저장하고 변환해서 사용할 수 있다.
        ** 참고 : https://stackoverflow.com/questions/10557360/convert-xlsx-to-csv-in-linux-with-command-line

*      엑셀 파일은 *.xlsx 로 저장하고, 우분투 리눅스에서는
*          $ sudo apt install catdoc
*      위 명령으로 프로그램을 설치 한 후에,
*          $ xlsx2csv my_file.xlsx -s 2 second_sheet.csv
*      이런 명령어로 xlsx 파일을 csv 로 변환 한 후, csv2uke 명령으로 UKE파일을 만들 수 있겠다.
*       - by ccash.
*
*************************************/


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define READLINE_MAX    1024         // 파일 한 라인 당 512 바이트는 넘지 않는 것으로 한다.
#define USAGE_PRINT     printf("\n\n  USAGE: csv2uke [CSV-file] [uke-filename]\n  <WARNING> uke-file will be overwritten !!\n\n");


int readline(FILE *fp, char *buffer);
int parse_line(char *buffer);
int set_bpm(char *bpm_str);
int set_quaver(char *quaver_str);

int set_start_offset(char *offset_str);
int get_note_from_line(char *chord_line);
void write_uke_file();
char *convert_note_g(char *str_pos);
char *convert_note_c(char *str_pos);
char *convert_note_e(char *str_pos);
char *convert_note_a(char *str_pos);
int  isNoNotedata(char g, char c, char e, char a);   // 모든 노트에 데이터가 없다면(연주할 음이 없으면) 출력을 SKIP 판단


////////////////////////////
typedef struct _note_data {
    long time_stamp;
    char chord[16];
    char g[8];
    char c[8];
    char e[8];
    char a[8];
    char stroke[8];
    char technic[8];

    char g_finger[8];
    char c_finger[8];
    char e_finger[8];
    char a_finger[8];

    char notedata_string[64];

    char lyric[64];
} NOTE_ONE;

char category[64];
char title[128];
char create_date[128];
char source[READLINE_MAX];
char thumbnail[READLINE_MAX];
char ukulele_tune[64];
char author[64];
char basic_beat[64];
//char author_comment[READLINE_MAX];
char author_note[READLINE_MAX];
char comment[READLINE_MAX];

////////////////////////////
FILE *in_f, *out_f;
int start_offset = 0;         // BPM과 음표길이에 따른 timestamp 값을 증가시키는 용도
float bpm = 0;         // BPM과 음표길이에 따른 timestamp 값을 증가시키는 용도
int semiquaver_base = 0;    // 악보데이터(엑셀,CSV)가 16분 음표 기반인지 8분 음표 기반인지 플래그.
int time_stamp = 0;         // BPM과 음표길이에 따른 timestamp 값을 증가시키는 용도
int beat_length_msec = 0;   // BPM을 기준으로 8분음표 길이의 msec 값.   #BPM를 읽을 때 계산해 둠.
float uke_version = 1.0f;


int main(int argc, char *argv[] )
{
    char line_buf[READLINE_MAX];

    if (argc<3)
    {   USAGE_PRINT;    //
        return(0);
    }

    in_f = fopen( argv[1], "rt" );
    if (in_f==NULL)
    {   printf("\n ERROR: input-file open error !!\n");
        USAGE_PRINT;    //
        return(-1);
    }

    out_f = fopen( argv[2], "wt" );
    if (out_f==NULL)
    {   printf("\n ERROR: output-file create (or open) error !!\n");
        USAGE_PRINT;    //
        return(-1);
    }
    // starting JSON-data
    fprintf(out_f, "{\n" );
    //      start note -data
    fprintf(out_f, "  \"notes\":[\n" );

    ///////////////////////////
    printf("\n\t<< Start CSV-file parsing !! >>\n\n");
    while ( readline(in_f, line_buf) >= 0 )  //  한 줄 읽기가 실패 할 때 까지 - 라인에 에러가 있거나 파일의 끝 까지..
    {
        ////  printf("line_buf: %s\n", line_buf );
        parse_line(line_buf);
    }
    // end of 'notes'
    fprintf(out_f, "  ],\n" );

    // 파싱이 다 끝난 다음에 나머지 정보들을 기록 하자.
    write_uke_file();
    ///////////////////////////

    // closing JSON-data
    fprintf(out_f, "}\n" );

    fclose(out_f);
    printf("\n\n\t-------- done. --------\n\n");
    return 0;
}


int readline(FILE *fp, char *buffer)
{   char *ret_ptr;
    int  len;

    ret_ptr = fgets(buffer, READLINE_MAX, fp);
    if (ret_ptr == NULL)    // 파일 읽기 에러.
    {   return -1;
    }
    len = strlen(buffer);
    if (len==0)
        return 0;

    while(*ret_ptr != '\0') {
      if (*ret_ptr == '\"')
        *ret_ptr = ' ';
      ret_ptr++;
    }

    return len;
}

int get_token(char *buffer, char *token)
{   int pos = 0;
    while (buffer[pos] != ',')
    {   if (buffer[pos] == '\0')
            break;
       if (buffer[pos] == '\n')
            break;
        token[pos] = buffer[pos];
        pos++;
    }
    token[pos] = '\0';
    return pos;
}

char *remove_white(char *str)
{   int pos = 0;
    while( *str != '\0' )
    {   if (isspace(*str))
	    str++;
	else
	    break;
    }
    return str;
}

void str_lowercase(char* beg)
{
    while (*beg++ = toupper(*beg));
}

/****************************************
    ** 엑셀파일(csv)형식의 예.

    #chord,,,,,,,F,,,,,,C,,,,,;
    #G,,,,,,,0,,3,,,0,,,3,,,;
    #C,,,,,1,3,1,1,,,,,3,,,,3,;
    #E,,,,,,,0,,,,,,0,,,,,;
    #A,,,,,,,2,,,,,,0,,,,,;
    #가사,,,,,욘,,데,이,루,,,무,네,,노,,도,;
    #chord,Dm,,,,,,Am,,,,,,Bb,,,,,;
    #G,,,0,,,,,,,,0,,,,,,,;
    #C,1,,,,,1,0,,,,,,,,0,,1,3;
    #E,2,2,,,,,0,,,,,,2,,,,,;
    #A,2,,,,,,2,,,,,,3,,,,,;
    #가사,코,카,오,,,쿠,데,,,,이,,쯔,,모, ,코,코;

    ** 설명 :
    **   1라인 문장의 1번째 글자가 #이어야만 한다. 그렇지 않으면 모두 무시.
    **   #BPM, #beat, #title, #start_offset #chord 는 필수.
    **   #chord 아래에 #G, #C, #E, #A 도 반드시 순서대로 나와야 하고 필수.
    **   #가사 는 옵션. 없어도 된다.
    **   , (콤마)로 구분되는 것은 1개당 8분음표 길이
          (--> bpm 에 의해 timestamp 값으로 바꿔야 함)
          (--> 별도 명령어을 지정해서 콤마 하나의 길이를 16분음표 로 바꿀 수 있도록 할 필요가 있다.)
    **   악보데이터 (#chord 및 #G, #C, #E, #A) 는 문자열이 ; 로 끝난다.

*****************************************/

int parse_line(char *buffer)
{
    int token_len = 0;
    int retValue = 0;   // no Error
    char *str_ptr;
    char token[READLINE_MAX];

    // printf("]] %s\n", buffer );         getchar();

    if (buffer[0] != '#')
    {   //  printf("%s has no '#'\n", buffer );
        return 0;       // #으로 시작하지 않는다면 무시. - 몽땅 코멘트로 취급
    }
    str_ptr = buffer+1;

        // 곡 제목..
    if ( strncmp( str_ptr, "title", 5) == 0 )
    {   token_len =  get_token(str_ptr+5+1, token);
        printf("Title is : %s \n", token );
        strcpy(title, token);
    } else if ( strncmp( str_ptr, "제목", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("Title is : %s \n", token );
        strcpy(title, token);
    }

    else if ( strncmp( str_ptr, "category", 8) == 0 )
    {   token_len =  get_token(str_ptr+8+1, token);
        printf("category is : %s \n", token);
        strcpy(category, token);
    } else if ( strncmp( str_ptr, "카테고리", 12) == 0 )
    {   token_len =  get_token(str_ptr+12+1, token);
        printf("category is : %s \n", token);
        strcpy(category, token);
    }

        //  source - 음악파일 (MP3) 또는 동영상 파일의 URL.
    else if ( strncmp( str_ptr, "source", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("source Music is : %s \n", token);
        strcpy(source, token);
    } else if ( strncmp( str_ptr, "음악파일", 12) == 0 )
    {   token_len =  get_token(str_ptr+12+1, token);
        printf("source Music is : %s \n", token);
        strcpy(source, token);
    }

        //  thumbnail - 연주곡 리스트의 아이콘들을 준비함..
    else if ( strncmp( str_ptr, "thumbnail", 9) == 0 )
    {   token_len =  get_token(str_ptr+9+1, token);
        printf("thumbnail is : %s \n", token);
        strcpy(thumbnail, token);
    } else if ( strncmp( str_ptr, "앨범표지", 12) == 0 )
    {   token_len =  get_token(str_ptr+12+1, token);
        printf("thumbnail is : %s \n", token);
        strcpy(thumbnail, token);
    }

    // else if ( strncmp( str_ptr, "작성자메모", 15) == 0 )
    // {   token_len =  get_token(str_ptr+15+1, token);
    //     printf("Author Note : %s \n", token);
    //     strcpy(author_note, token);
    // } else if ( strncmp( str_ptr, "author_note", 11) == 0 )
    // {   token_len =  get_token(str_ptr+11+1, token);
    //     printf("author notes : %s \n", token);
    //     strcpy(author_note, token);
    // }
    else if ( strncmp( str_ptr, "작성자", 9) == 0 )
    {   token_len =  get_token(str_ptr+9+1, token);
        printf("Author is : %s \n", token);
        strcpy(author, token);
    } else if ( strncmp( str_ptr, "author", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("author is : %s \n", token);
        strcpy(author, token);
    }
    else if ( strncmp( str_ptr, "설명", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("comment is : %s \n", token);
        strcpy(comment, token);
    } else if ( strncmp( str_ptr, "comment", 7) == 0 )
    {   token_len =  get_token(str_ptr+7+1, token);
        printf("comment is : %s \n", token);
        strcpy(comment, token);
    }

        // BPM - 이 것으로 timestamp 를 계산할 것이므로 매우 중요.
    else if ( strncmp( str_ptr, "bpm", 3) == 0 )
    {   token_len =  get_token(str_ptr+3+1, token);
        printf("BPM is : %s \n", token);
        set_bpm(token);
    } else if ( strncmp( str_ptr, "BPM", 3) == 0 )
    {   token_len =  get_token(str_ptr+3+1, token);
        printf("BPM is : %s \n", token);
        set_bpm(token);
    }
        // quaver - 이것은 8분음표 기반인지 16분 음표 기반인지를 판단. 이것으로 timestamp 를 계산할 것이므로 매우 중요.
    else if ( strncmp( str_ptr, "quaver", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
        set_quaver(token);
        printf("Beat is : %s \n", token);
    } else if ( strncmp( str_ptr, "8분음표", 10) == 0 )
    {   token_len =  get_token(str_ptr+10+1, token);
        set_quaver(token);
        printf("Beat is : %s \n", token);
    }

        // 박자 - 1 마디 안에 들어 갈 박자의 수 - 엑셀파일의 1개 셀은 (기본적으로) 8분음표를 기준으로 했음.
    else if ( strncmp( str_ptr, "beat", 4) == 0 )
    {   token_len =  get_token(str_ptr+4+1, token);
        printf("beat is : %s \n", token);
        strcpy(basic_beat, token);
    } else if ( strncmp( str_ptr, "박자", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("beat is : %s \n", token);
        strcpy(basic_beat, token);
    }
/*
        // 엑셀 파일의 가로 1 줄에 포함된 마디 수.
    else if ( strncmp( str_ptr, "measure", 7) == 0 )
    {   token_len =  get_token(str_ptr+7+1, token);
        printf("measure in a line is : %s \n", token);
    } else if ( strncmp( str_ptr, "마디", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("1행 당 마디 수 : %s \n", token);
    }
*/
        // Start_offset.
    else if ( strncmp( str_ptr, "start_offset", 12) == 0 )
    {   token_len =  get_token(str_ptr+12+1, token);
        printf("start_offset is : %s \n", token);
        set_start_offset(token);
    } else if ( strncmp( str_ptr, "시작위치", 12) == 0 )
    {   token_len =  get_token(str_ptr+12+1, token);
        printf("시작시간위치 is : %s \n", token);
        set_start_offset(token);
    }
    // UKE형식 버전 비교..
    else if ( strncmp( str_ptr, "docversion", 10) == 0 )
    {   token_len =  get_token(str_ptr+10+1, token);
        printf("UKE버전: %s \n", token);
        sscanf(token, "%f", &uke_version);
    } else if ( strncmp( str_ptr, "문서버전", 12) == 0 )
    {   token_len =  get_token(str_ptr+12+1, token);
        printf("UKE버전: %s \n", token);
        sscanf(token, "%f", &uke_version);
    }
    // UKE형식 버전 비교..
    else if ( strncmp( str_ptr, "tuning", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("조율방법: %s \n", token);
        strcpy(ukulele_tune, token);
        str_lowercase(ukulele_tune);
    } else if ( strncmp( str_ptr, "조율방법", 12) == 0 )
    {   token_len =  get_token(str_ptr+12+1, token);
        printf("조율방법: %s \n", token);
        strcpy(ukulele_tune, token);
        str_lowercase(ukulele_tune);
    }


    else if ( strncmp( str_ptr, "chord", 5) == 0 )
    {   token_len =  get_token(str_ptr+5+1, token);
//        printf("\nchord is : %s \n", token);
        retValue = get_note_from_line(str_ptr+5+1);
    } else if ( strncmp( str_ptr, "코드", 6) == 0 )
    {   token_len =  get_token(str_ptr+6+1, token);
//        printf("\nchord is : %s \n", token);
        retValue = get_note_from_line(str_ptr+6+1);
    }

    else {
//        printf("could not get TAG: %s\n", str_ptr);
        printf("Unknown TAG: %s\n", buffer );
    }

    // Read Error Check
    if (retValue < 0 ) {    // get_note_from_line()  returned error.
      printf("...get_note_from_line() return %d\n", retValue);
    }


}

int set_bpm(char *bpm_str)
{   bpm = atof(bpm_str);

    if (semiquaver_base)        // 악보 데이터가 16분 음표 기반인지 아닌지 판단.
        beat_length_msec = (float)30000.0 / (bpm*2);   // BPM을 기준으로 16분음표 길이의 msec 값.   #BPM를 읽을 때 계산해 둠.
    else
        beat_length_msec = (float)30000.0 / bpm;   // BPM을 기준으로 8분음표 길이의 msec 값.   #BPM를 읽을 때 계산해 둠.

    printf("quaver = %d,  bpm=%5.1f, beat_length=%d \n", semiquaver_base, bpm,beat_length_msec );

    return 0;
}

int set_quaver(char *quaver_str)
{
    //printf("quaver_str = %s\n", quaver_str );
    if ( strncmp(quaver_str, "semiquaver", 10)==0 )     // 16분 음표 기반인지 판단.
    {   semiquaver_base = 1;
    } else
    {   semiquaver_base = 0;
    }

    if (bpm != 0)       // 이미 bpm 값이 설정 되어 있으면 재설정.
    { if (semiquaver_base)
      { beat_length_msec = (float)30000.0 / (bpm*2);   // BPM을 기준으로 8분음표 길이의 msec 값.   #BPM를 읽을 때 계산해 둠.  1qns 60ch=60000 을 8분음표 기준이므로 반으로 나누어 30000 으로 하여 계산.
      } else
      { beat_length_msec = (float)30000.0 / bpm;   // BPM을 기준으로 8분음표 길이의 msec 값.   #BPM를 읽을 때 계산해 둠.
      }
    }
    printf("semiquaver_base = %d, quaver = %s,  bpm=%5.1f, beat_length=%d \n", semiquaver_base, quaver_str, bpm,beat_length_msec );

    return 0;
}

int set_start_offset(char *offset_str)
{   int offset = atoi(offset_str);
    time_stamp = start_offset = offset;   // BPM과 음표길이에 따른 timestamp 값을 증가시키는 용도
    return 0;
}


/**********************
    #chord 를 발견하게 되면, 그 아랫 줄의 #G, #C, #E, #A 와 #가사 까지도 모두 순차적으로 읽어 와서
        라인을 모두 파싱하는 동작을 한다.
***********************/

long noteCount = 0;   // JSON배열을 만들 때, 아이템의 갯수.(갯수가 0이라는 건 첫번째 아이템이므로 ,를 빼야 한다.)
long chordCount = 0;   // 첫번째 note 데이터에서는 , 로 구분 안하고 시작. 맨 처음이 아니면 , 를 찍고 시작.

int get_note_from_line(char *chord_line) {
    char a_line[READLINE_MAX], *a_ptr;
    char e_line[READLINE_MAX], *e_ptr;
    char c_line[READLINE_MAX], *c_ptr;
    char g_line[READLINE_MAX], *g_ptr;
    char lyric_line[READLINE_MAX], *l_ptr;
    char stroke[READLINE_MAX], *stroke_ptr;
    char technic[READLINE_MAX], *technic_ptr;
    int  token_len = 0;
    char token[READLINE_MAX];

    NOTE_ONE    note;

    if ( readline(in_f, a_line) < 0 )  //  1번줄(A) 악보 읽기가 실패하면 에러 리턴.
    {   return  -1;
    }
    if ( readline(in_f, e_line) < 0 )  //  2번줄(E) 악보 읽기가 실패하면 에러 리턴.
    {   return  -2;
    }
    if ( readline(in_f, c_line) < 0 )  //  3번줄(C) 악보 읽기가 실패하면 에러 리턴.
    {   return  -3;
    }
    if ( readline(in_f, g_line) < 0 )  //  4번줄(G) 악보 읽기가 실패하면 에러 리턴.
    {   return  -4;
    }
    if ( readline(in_f, lyric_line) < 0 )  //  가사 읽기가 실패하는 건 무시.
    {   printf("Read for lyric was failed.");
    }
    // printf("  # lyric_line : %s \n", lyric_line);

    if (uke_version >= 2.0f) {
      // printf("uke_ver2.0 has to read for #stroke & #technic line \n");

      if ( readline(in_f, stroke) < 0 )  //  스트로크 방향을 지정하지 않는 것도 무시.
      {   printf("Read for #stroke was failed.");
      }

      if ( readline(in_f, technic) < 0 )  //  sliding, hammering-on 등의 연주기교를 지정하지 않는 것도 무시.
      {   printf("Read for #technic was failed.");
      }

      // printf(" - #stroke = %s \n - #technic = %s\n", stroke, technic );
    }

    if ( (g_line[0] != '#') || (g_line[1] != 'G') )
    {   printf("g_line has Error: %s\n", g_line );
        return -4;
    }
    if ( (c_line[0] != '#') || (c_line[1] != 'C') )
    {   printf("c_line has Error: %s\n", c_line );
        return -3;
    }
    if ( (e_line[0] != '#') || (e_line[1] != 'E') )
    {   printf("e_line has Error: %s\n", e_line );
        return -2;
    }
    if ( (a_line[0] != '#') || (a_line[1] != 'A') )
    {   printf("a_line has Error: %s\n", a_line );
        return -1;
    }
    g_ptr = g_line+3;       // "#G," 를 skip
    c_ptr = c_line+3;       // "#C," 를 skip
    e_ptr = e_line+3;       // "#E," 를 skip
    a_ptr = a_line+3;       // "#A," 를 skip
    l_ptr = lyric_line+8;   // "#가사," 를 skip
    if (uke_version >= 2.0f) {
      stroke_ptr = stroke+8;  // "#stroke," 를 skip
      technic_ptr = technic+9;  // "#technic," 를 skip
    } else {
      stroke_ptr = technic_ptr = NULL;
    }


    printf("\t%s\t%s\t%s\t%s\t%s\n", chord_line, a_ptr, e_ptr, c_ptr, g_ptr );
    //getchar();

//    chordCount = 0;
    while( *chord_line != '\0' ) {

        if ( *chord_line == ';' )       // 1 라인 버퍼의 끝
            break;

////////////////////////////////
// 토큰으로 읽기
        //memset(&note, '\0', sizeof(note) );
        note.time_stamp = time_stamp;

        token_len = get_token(chord_line, token);
        if (token_len > 0)
        {   strcpy(note.chord, remove_white(token));
        }
        else
        {   note.chord[0] = '\0';
        }
        chord_line += token_len+1;      // 다음 토큰(코드)로 포인터 이동.

        token_len = get_token(g_ptr, token);
        if (token_len > 0)
        {   strcpy(note.g, remove_white(token));
        }
        else
        {   note.g[0] = '\0';
        }
        g_ptr += token_len+1;      // 다음 토큰(G)로 포인터 이동.

        token_len = get_token(c_ptr, token);
        if (token_len > 0)
        {   strcpy(note.c, remove_white(token));
        }
        else
        {   note.c[0] = '\0';
        }
        c_ptr += token_len+1;      // 다음 토큰(C)로 포인터 이동.

        token_len = get_token(e_ptr, token);
        if (token_len > 0)
        {   strcpy(note.e, remove_white(token));
        }
        else
        {   note.e[0] = '\0';
        }
        e_ptr += token_len+1;      // 다음 토큰(E)로 포인터 이동.

        token_len = get_token(a_ptr, token);
        if (token_len > 0)
        {   strcpy(note.a, remove_white(token));
        }
        else
        {   note.a[0] = '\0';
        }
        a_ptr += token_len+1;      // 다음 토큰(A)로 포인터 이동.

        token_len = get_token(l_ptr, token);
        if (token_len > 0)
        {   strcpy(note.lyric, token);
        }
        else
        {   note.lyric[0] = '\0';
        }
        l_ptr += token_len+1;      // 다음 토큰(가사)로 포인터 이동.

        if (uke_version >= 2.0f) {
          token_len = get_token(stroke_ptr, token);
          if (token_len > 0)
          {   strcpy(note.stroke, token);
          }
          else
          {   note.stroke[0] = '\0';
          }
          stroke_ptr += token_len+1;      // 다음 토큰(가사)로 포인터 이동.

          token_len = get_token(technic_ptr, token);
          if (token_len > 0)
          {   strcpy(note.technic, token);
          }
          else
          {   note.technic[0] = '\0';
          }
          technic_ptr += token_len+1;      // 다음 토큰(가사)로 포인터 이동.
        }

/////////////////////////////////////////
//        make_note_data(&note);

        //// 실제 음계 데이터를 파일에 써 넣기.
        if ( (note.chord[0] == '\0')&&
                (note.g[0]=='\0')&&
                (note.c[0]=='\0')&&
                (note.e[0]=='\0')&&
                (note.a[0]=='\0')&&
                (note.lyric[0]=='\0') )
        {       // 연주할 음이 없는 상태 - 쉼표, 쉬는 마디, 이전 음계의 연속, 등..
        }
        else
        {
            if ( isNoNotedata( note.g[0], note.c[0], note.e[0], note.a[0]) )
            {       // 기록할 note 가 하나도 없으면 기록하지 않는다.
                continue;
            }

            if ( chordCount > 0 )             // JSON 배열 형식에서 맨 마지막엔 , 를 찍으면 안되기 때문에.. 첫번째 이후의 아이템은 , 를 찍으며 시작하도록 함.
            {   fprintf(out_f, ",\n" );
            }

            fprintf(out_f, "\t{\n" );
            fprintf(out_f, "\t  \"timestamp\":%d,\n" ,time_stamp );
            fprintf(out_f, "\t  \"chord\":\"%s\",\n" ,note.chord );
            fprintf(out_f, "\t  \"tab\":[" );

            noteCount = 0;
            if (note.g[0] != '\0')
            {   if (noteCount>0) fprintf(out_f, ",");
                fprintf(out_f, "\"G%s\"", note.g);
                noteCount++;
            }
            if (note.c[0] != '\0')
            {   if (noteCount>0) fprintf(out_f, ",");
                fprintf(out_f, "\"C%s\"", note.c);
                noteCount++;
            }
            if (note.e[0] != '\0')
            {   if (noteCount>0) fprintf(out_f, ",");
                fprintf(out_f, "\"E%s\"", note.e);
                noteCount++;
            }
            if (note.a[0] != '\0')
            {   if (noteCount>0) fprintf(out_f, ",");
                fprintf(out_f, "\"A%s\"", note.a);
                noteCount++;
            }
            fprintf(out_f, "],\n" );

            fprintf(out_f, "\t  \"note\":[" );
            noteCount = 0;
            if (note.g[0] != '\0')
            {   if (noteCount>0) fprintf(out_f, ",");
                fprintf(out_f, "\"%s\"", convert_note_g(note.g) );
                noteCount++;
            }
            if (note.c[0] != '\0')
            {   if (noteCount>0) fprintf(out_f, ",");
                fprintf(out_f, "\"%s\"", convert_note_c(note.c) );
                noteCount++;
            }
            if (note.e[0] != '\0')
            {   if (noteCount>0) fprintf(out_f, ",");
                fprintf(out_f, "\"%s\"", convert_note_e(note.e) );
                noteCount++;
            }
            if (note.a[0] != '\0')
            {   if (noteCount>0) fprintf(out_f, ",");
                fprintf(out_f, "\"%s\"", convert_note_a(note.a) );
                noteCount++;
            }
            fprintf(out_f, "]" );

            if (note.lyric[0] != '\0')
                fprintf(out_f, ",\n\t  \"lyric\":\"%s\"" ,note.lyric );
            else {
              if (uke_version < 2.0f)   // 2.0 부터는 아래의 stroke 와 technic 을 확인한 이후에 처리 하게 되므로.
                fprintf(out_f, "\n");
            }

            if (uke_version >= 2.0f) {
              if (note.stroke[0] != '\0')
                  fprintf(out_f, ",\n\t  \"stroke\":\"%s\"" ,note.stroke );
              // else
              //     fprintf(out_f, "\n");
              if (note.technic[0] != '\0')
                  fprintf(out_f, ",\n\t  \"technic\":\"%s\"" ,note.technic );
              // else
                  // fprintf(out_f, "\n");
            }

            fprintf(out_f, "\n\t}" );
//            printf("\n");
            chordCount++;   // 저장되는 코드의 갯수를 센다. - 맨 첫번째 것만 , 를 안찍는 방법.
        }

        if (uke_version >= 2.0f) {
          if (note.technic[0] == '/') {     // 일시적으로 반음만 처리.
            time_stamp += (beat_length_msec / 2);
          } else {
            time_stamp += beat_length_msec;
          }
        }
    }
}
/*
    {
      "timestamp":4572,
      "chord":"Bb"
      "tab":[   "G3a","C2m","E1i"    ],
      "lyric":"ム",
      "note":[  "A4#","D4","F4"      ],
    },
*/

int  isNoNotedata(char g, char c, char e, char a)   // 모든 노트에 데이터가 없다면(연주할 음이 없으면) 출력을 SKIP 판단
{
    if ( (g == '\0')&& (c == '\0')&& (e == '\0')&& (a == '\0') )
        return 1;
    else
        return 0;
}


void write_uke_file()
{
    fprintf(out_f, "\n" );
    fprintf(out_f, "  \"title\":\"%s\",\n" , title );
    fprintf(out_f, "  \"author\":\"%s\",\n" , author );
    //fprintf(out_f, "  \"author_comment\":\"%s\",\n" , author_comment );
    fprintf(out_f, "  \"author_note\":\"%s\",\n" , author_note );
    fprintf(out_f, "  \"category\":\"%s\",\n" , category );
    fprintf(out_f, "  \"comment\":\"%s\",\n" , comment );
    fprintf(out_f, "  \"create_date\":\"----/--/--\",\n" );
    // 아래의 내용들은 *.uke 파일로써 반드시 필요한 내용들.
    fprintf(out_f, "  \"source\":\"%s\",\n" , source );
    fprintf(out_f, "  \"thumbnail\":\"%s\",\n" , thumbnail );
    fprintf(out_f, "  \"start_offset\":\"%d\",\n" , start_offset );
    fprintf(out_f, "  \"basic_beat\":\"%s\",\n" , basic_beat );
    fprintf(out_f, "  \"bpm\":\"%5.1f\"\n" , bpm );
}

/*void make_note_data(NOTE_ONE *n)
{

    n->notedata_string
}*/

#define NOTENAME_LENG 34
#define MAX_FLET 17
char *note_names[NOTENAME_LENG] = { "G3", "G#3", "A3", "A#3", "B3",         // 낮은 음으로 G 현을 조정했을 경우..
                                    "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
                                    "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
                                    "C5", "C#5", "D5", "D#5", "E5" };
char **G_note = &note_names[12];
char **C_note = &note_names[5];
char **E_note = &note_names[9];
char **A_note = &note_names[14];

char *convert_note_g(char *str_pos)
{
    int flet_num;
    sscanf(str_pos, "%d", &flet_num);
    if ((flet_num < 0) || (flet_num > MAX_FLET))
    {   printf("parse_G has Error occured. Wrong flet number !! (%d) from [%s]\n", flet_num, str_pos );
        return NULL;
    }
    return G_note[flet_num];
}
char *convert_note_c(char *str_pos)
{
    int flet_num;
    sscanf(str_pos, "%d", &flet_num);
    if ((flet_num < 0) || (flet_num > MAX_FLET))
    {   printf("parse_C has Error occured. Wrong flet number !! (%d) from [%s]\n", flet_num, str_pos );
        return NULL;
    }
    return C_note[flet_num];
}
char *convert_note_e(char *str_pos)
{
    int flet_num;
    sscanf(str_pos, "%d", &flet_num);
    if ((flet_num < 0) || (flet_num > MAX_FLET))
    {   printf("parse_E has Error occured. Wrong flet number !! (%d) from [%s]\n", flet_num, str_pos );
        return NULL;
    }
    return E_note[flet_num];
}
char *convert_note_a(char *str_pos)
{
    int flet_num;
    sscanf(str_pos, "%d", &flet_num);
    if ((flet_num < 0) || (flet_num > MAX_FLET))
    {   printf("parse_A has Error occured. Wrong flet number !! (%d) from [%s]\n", flet_num, str_pos );
        return NULL;
    }
    return A_note[flet_num];
}
