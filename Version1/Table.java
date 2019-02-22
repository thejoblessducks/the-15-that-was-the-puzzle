public class Table{
    private String str_table="";
    private char last_move;
    private int row,col;
    private int[][] table;
    //Constructor (options)
        public void Table(int[][] table, char last_move){
            this.last_move=last_move; //Saves the move used to reach this legal configuration
            this.table = new int[4][4];
            for(int i=0;i<4;i++) //move through rows
                for(int j=0;j<4;j++){
                    if(table[i][j]==0){this.row=i;this.col=j;}
                    this.str_table+=table[i][j];
                    this.table[i][j]=table[i][j];
                }
        }
        public void Table(int[] array_table, char last_move, String str_table){
            this.last_move=last_move;
            this.str_table=str_table;
            this.table=new int[4][4];
            int i=0; //index for the array positions
            for(int j=0;j<4;j++)
                for(int k=0;k<4;k++){
                    if(array_table[i]==0){this.row=j;this.col=k;}
                    this.table[j][k]=array_table[i];
                    i++;
                }
        }

    //Getters
        public int getBlankRow(){return this.row;}
        public int getBlankCol(){return this.col;}
        public char getLastMove(){return this.last_move;}
        public String getTableString(){return this.str_table;}
}
