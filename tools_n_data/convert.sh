####
## Convert xlsx to uke
##
##  Usage : ./convert.sh InputExcel.xlsx OutputUke.uke
##

## xlsx2csv :  출처 : https://stackoverflow.com/questions/10557360/convert-xlsx-to-csv-in-linux-with-command-line
##   $ sudo apt install gnumeric
##   $ ssconvert InputExcel.xlsx OutputCsv.csv

echo "================================================"
echo "  USAGE : ./convert.sh [input.xls] [output.uke] "
echo "      ****  you need sub-folder named 'temp.csv'"
echo "================================================"

ssconvert $1 temp.csv/$1.csv
./csv2uke temp.csv/$1.csv $2

##  Completed!  Enjoy it !!
