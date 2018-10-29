#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define READLINE_MAX    512          // 파일 한 라인 당 512 바이트는 넘지 않는 것으로 한다. 


int readline(FILE *fp, char *buffer);
int parse_line(char *buffer);
int set_bpm(char *bpm_str);
int set_start_offset(char *offset_str);
int get_note_from_line(char *chord_line);
void write_uke_file();
char *convert_note_g(char *str_pos);
char *convert_note_c(char *str_pos);
char *convert_note_e(char *str_pos);
char *convert_note_a(char *str_pos);


////////////////////////////
typedef struct _note_data {
    long time_stamp;
    char chord[16];
    char g[8];
    char c[8];
    char e[8];
    char a[8];

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
char author[64];
char author_comment[READLINE_MAX];
char author_note[READLINE_MAX];
char comment[READLINE_MAX];

////////////////////////////
FILE *in_f, *out_f;
int start_offset = 0;         // BPM과 음표길이에 따른 timestamp 값을 증가시키는 용도
int bpm = 0;         // BPM과 음표길이에 따른 timestamp 값을 증가시키는 용도
int time_stamp = 0;         // BPM과 음표길이에 따른 timestamp 값을 증가시키는 용도
int beat_length_msec = 0;   // BPM을 기준으로 8분음표 길이의 msec 값.   #BPM를 읽을 때 계산해 둠.


int main(int argc, char *argv[] )
{
    char line_buf[READLINE_MAX];

    if (argc<2) 
    {   printf("\n\n  USAGE: csv2uke [CSV-file] [uke-filename]\n      WARNING: uke-file will be overwritten !!\n\n");
        return(0);
    }

    in_f = fopen( argv[1], "rt" );
    if (in_f==NULL) 
    {   printf("\n ERROR: input-file open error !!\n");
        return(-1);
    }

    out_f = fopen( argv[2], "wt" );
    if (out_f==NULL) 
    {   printf("\n ERROR: output-file create (or open) error !!\n");
        return(-1);
    }
    // starting JSON-data
    fprintf(out_f, "{\n" );
    //      start note -data
    fprintf(out_f, "  \"notes\":[\n" );

    ///////////////////////////
    printf("\n\t<< Start CSV-file parsing !! >>\n");
    while ( readline(in_f, line_buf) >= 0 )  //  한 줄 읽기가 실패 할 때 까지 - 라인에 에러가 있거나 파일의 끝 까지..
    {
//        printf("line_buf: %s\n", line_buf );
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
    printf("\n\n\t 아직 남아 있는 할 일. - 없음. 다 했음.\n");
    printf("- 소스코드 도 정리좀 해서 모듈화 하자.\n\n\n");
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

/****************************************
    ** 엑셀파일(csv)형식의 예.

    #chord,,,,,,,F,,,,,,C,,,,,
    #G,,,,,,,0,,3,,,0,,,3,,,
    #C,,,,,1,3,1,1,,,,,3,,,,3,
    #E,,,,,,,0,,,,,,0,,,,,
    #A,,,,,,,2,,,,,,0,,,,,
    #가사,,,,,욘,,데,이,루,,,무,네,,노,,도,
    #chord,Dm,,,,,,Am,,,,,,Bb,,,,,
    #G,,,0,,,,,,,,0,,,,,,,
    #C,1,,,,,1,0,,,,,,,,0,,1,3
    #E,2,2,,,,,0,,,,,,2,,,,,
    #A,2,,,,,,2,,,,,,3,,,,,
    #가사,코,카,오,,,쿠,데,,,,이,,쯔,,모, ,코,코

    ** 설명 :  
    **   1라인 문장의 1번째 글자가 #이어야만 한다. 그렇지 않으면 모두 무시. 
    **   #BPM, #beat, #title, #start_offset #chord 는 필수.
    **   #chord 아래에 #G, #C, #E, #A 도 반드시 순서대로 나와야 하고 필수.
    **   #가사 는 옵션. 없어도 된다. 
    **   , (콤마)로 구분되는 것은 1개당 8분음표 길이
          (--> bpm 에 의해 timestamp 값으로 바꿔야 함)
          (--> 별도 명령어을 지정해서 콤마 하나의 길이를 16분음표 로 바꿀 수 있도록 할 필요가 있다.)

*****************************************/


int parse_line(char *buffer)
{
    int token_len = 0;
    char *str_ptr;
    char token[READLINE_MAX];

    if (buffer[0] != '#') 
    {   
//        printf("%s has no '#'\n", buffer );
        return 0;       // #으로 시작하지 않는다면 무시.
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

    else if ( strncmp( str_ptr, "작성자", 9) == 0 ) 
    {   token_len =  get_token(str_ptr+9+1, token);
        printf("Author is : %s \n", token);
        strcpy(author, token);
    } else if ( strncmp( str_ptr, "author", 6) == 0 ) 
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("author is : %s \n", token);
        strcpy(author, token);
    }
    else if ( strncmp( str_ptr, "코멘트", 9) == 0 ) 
    {   token_len =  get_token(str_ptr+9+1, token);
        printf("Author is : %s \n", token);
        strcpy(author_comment, token);
    } else if ( strncmp( str_ptr, "author_comment", 14) == 0 ) 
    {   token_len =  get_token(str_ptr+14+1, token);
        printf("author is : %s \n", token);
        strcpy(author_comment, token);
    }
    else if ( strncmp( str_ptr, "메모", 9) == 0 ) 
    {   token_len =  get_token(str_ptr+9+1, token);
        printf("Author is : %s \n", token);
        strcpy(author_note, token);
    } else if ( strncmp( str_ptr, "author_note", 14) == 0 ) 
    {   token_len =  get_token(str_ptr+14+1, token);
        printf("author is : %s \n", token);
        strcpy(author_note, token);
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

/*        // 박자 - 1 마디 안에 들어 갈 박자의 수 - 엑셀파일의 1개 셀은 (기본적으로) 8분음표를 기준으로 했음.
    else if ( strncmp( str_ptr, "beat", 4) == 0 ) 
    {   token_len =  get_token(str_ptr+4+1, token);
        printf("beat is : %s \n", token);
    } else if ( strncmp( str_ptr, "박자", 6) == 0 ) 
    {   token_len =  get_token(str_ptr+6+1, token);
        printf("beat is : %s \n", token);
    }

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
    } else if ( strncmp( str_ptr, "시작위치", 12) == 0 ) 
    {   token_len =  get_token(str_ptr+12+1, token);
        printf("시작시간위치 is : %s \n", token);
    }

    else if ( strncmp( str_ptr, "chord", 5) == 0 ) 
    {   token_len =  get_token(str_ptr+5+1, token);
//        printf("\nchord is : %s \n", token);
        get_note_from_line(str_ptr+5+1);
    } else if ( strncmp( str_ptr, "코드", 6) == 0 ) 
    {   token_len =  get_token(str_ptr+6+1, token);
//        printf("\nchord is : %s \n", token);
        get_note_from_line(str_ptr+6+1);
    }


    else {
//        printf("could not get TAG: %s\n", str_ptr);
        printf("Unknown TAG: %s\n", buffer );
    }
    
}

int set_bpm(char *bpm_str) 
{   bpm = atoi(bpm_str);
    beat_length_msec = 60000 / bpm;   // BPM을 기준으로 8분음표 길이의 msec 값.   #BPM를 읽을 때 계산해 둠.
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
    char g_line[READLINE_MAX], *g_ptr;
    char c_line[READLINE_MAX], *c_ptr;
    char e_line[READLINE_MAX], *e_ptr;
    char a_line[READLINE_MAX], *a_ptr;
    char lyric_line[READLINE_MAX], *l_ptr;
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

    if ( (g_line[0] != '#') || (g_line[1] != 'G') )
    {   return -4;
    }
    if ( (c_line[0] != '#') || (c_line[1] != 'C') )
    {   return -3;
    }
    if ( (e_line[0] != '#') || (e_line[1] != 'E') )
    {   return -2;
    }
    if ( (a_line[0] != '#') || (a_line[1] != 'A') )
    {   return -1;
    }
    g_ptr = g_line+3;       // "#G," 를 skip
    c_ptr = c_line+3;       // "#C," 를 skip
    e_ptr = e_line+3;       // "#E," 를 skip
    a_ptr = a_line+3;       // "#A," 를 skip
    l_ptr = lyric_line+8;   // "#가사," 를 skip


    printf("\t\tstart_Parsing ! : %s", chord_line );

//    chordCount = 0;
    while( *chord_line != '\0' ) {

        //memset(&note, '\0', sizeof(note) );        
        note.time_stamp = time_stamp;

        token_len = get_token(chord_line, token);
        if (token_len > 0)
        {   strcpy(note.chord, token);
        } 
        else 
        {   note.chord[0] = '\0';
        }
        chord_line += token_len+1;      // 다음 토큰(코드)로 포인터 이동.

        token_len = get_token(g_ptr, token);
        if (token_len > 0)
        {   strcpy(note.g, token);
        } 
        else 
        {   note.g[0] = '\0';
        }
        g_ptr += token_len+1;      // 다음 토큰(G)로 포인터 이동.

        token_len = get_token(c_ptr, token);
        if (token_len > 0)
        {   strcpy(note.c, token);
        } 
        else 
        {   note.c[0] = '\0';
        }
        c_ptr += token_len+1;      // 다음 토큰(C)로 포인터 이동.

        token_len = get_token(e_ptr, token);
        if (token_len > 0)
        {   strcpy(note.e, token);
        } 
        else 
        {   note.e[0] = '\0';
        }
        e_ptr += token_len+1;      // 다음 토큰(E)로 포인터 이동.

        token_len = get_token(a_ptr, token);
        if (token_len > 0)
        {   strcpy(note.a, token);
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
            //printf("\t%s \n", chord_line );
/*            printf("timestamp:%d, chord:%s, ", time_stamp, note.chord);
            if (note.g[0] != '\0')
                printf("G%s,", note.g);
            if (note.c[0] != '\0')
                printf("C%s,", note.c);
            if (note.e[0] != '\0')
                printf("E%s,", note.e);
            if (note.a[0] != '\0')
                printf("A%s,", note.a);
            if (note.lyric[0] != '\0')
                printf("(%s),", note.lyric);
*/
            if ( (note.g[0] != '\0')&& (note.c[0] != '\0')&& (note.e[0] != '\0')&& (note.a[0] != '\0') )
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
                fprintf(out_f, ",\n\t  \"lyric\":\"%s\"\n" ,note.lyric );
            else
                fprintf(out_f, "\n");

            fprintf(out_f, "\t}" );
//            printf("\n");
            chordCount++;   // 저장되는 코드의 갯수를 센다. - 맨 첫번째 것만 , 를 안찍는 방법.
        }
        time_stamp += beat_length_msec;
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

void write_uke_file() 
{
    fprintf(out_f, "\n" );
    fprintf(out_f, "  \"title\":\"%s\",\n" , title );
    fprintf(out_f, "  \"author\":\"%s\",\n" , author );
    fprintf(out_f, "  \"author_comment\":\"%s\",\n" , author_comment );
    fprintf(out_f, "  \"author_note\":\"%s\",\n" , author_note );
    fprintf(out_f, "  \"category\":\"%s\",\n" , category );
    fprintf(out_f, "  \"comment\":\"%s\",\n" , comment );
    fprintf(out_f, "  \"create_date\":\"----/--/--\",\n" );
    // 아래의 내용들은 *.uke 파일로써 반드시 필요한 내용들.
    fprintf(out_f, "  \"source\":\"%s\",\n" , source );
    fprintf(out_f, "  \"start_offset\":\"%d\",\n" , start_offset );
    fprintf(out_f, "  \"bpm\":\"%d\"\n" , bpm );
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
    {   printf("parse_G has Error occured. Wrong flet number !! (%d)\n", flet_num );
        return NULL;
    }
    return G_note[flet_num];
}
char *convert_note_c(char *str_pos) 
{
    int flet_num;
    sscanf(str_pos, "%d", &flet_num);
    if ((flet_num < 0) || (flet_num > MAX_FLET))
    {   printf("parse_C has Error occured. Wrong flet number !! (%d)\n", flet_num );
        return NULL;
    }
    return C_note[flet_num];
}
char *convert_note_e(char *str_pos) 
{
    int flet_num;
    sscanf(str_pos, "%d", &flet_num);
    if ((flet_num < 0) || (flet_num > MAX_FLET))
    {   printf("parse_E has Error occured. Wrong flet number !! (%d)\n", flet_num );
        return NULL;
    }
    return E_note[flet_num];
}
char *convert_note_a(char *str_pos) 
{
    int flet_num;
    sscanf(str_pos, "%d", &flet_num);
    if ((flet_num < 0) || (flet_num > MAX_FLET))
    {   printf("parse_A has Error occured. Wrong flet number !! (%d)\n", flet_num );
        return NULL;
    }
    return A_note[flet_num];
}

