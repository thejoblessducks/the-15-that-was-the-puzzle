class Table{
    private String str_table="";
    private int last_move;
        //0 ->initial
        //1 -1 -> Up Down
        //2 -2 -> Left Right
    private int row,col; //blank Position
    private int[][] table;
    //Constructor (options)
        public Table(int[][] table, int last_move){
            this.last_move=last_move; //Saves the move used to reach this legal configuration
            this.table = new int[4][4];
            for(int i=0;i<4;i++){ //move through rows
                for(int j=0;j<4;j++){
                    if(table[i][j]==0){this.row=i;this.col=j;}
                    this.str_table+=table[i][j];
                    this.table[i][j]=table[i][j];
                }
            }
        }
        public Table(int[] array_table, int last_move, String str_table){
            this.last_move=last_move;
            this.str_table=str_table;
            this.table=new int[4][4];
            int i=0; //index for the array positions
            for(int j=0;j<4;j++){
                for(int k=0;k<4;k++){
                    if(array_table[i]==0){this.row=j;this.col=k;}
                    this.table[j][k]=array_table[i];
                    i++;
                }
            }
        }

    //Getters
        public int getBlankRow(){return this.row;}
        public int getBlankCol(){return this.col;}
        public int getLastMove(){return this.last_move;}
        public int[][] getTable(){ //returns a coppy table
            int[][] tmp=new int[4][4];
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++)
                    tmp[i][j]=this.table[i][j];
            }
            return tmp;
        }
        public String getString(){return this.str_table;}
}
