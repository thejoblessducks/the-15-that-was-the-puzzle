class Table{
    //instead of using a table we use an array, saves more memory;
    private String str_table="";
    private int row,col; //blank Position
    private int[] table;
    private int last_move;
        //0 ->initial
        //1 -1 -> Up Down
        //2 -2 -> Left Right
    //Constructor
        public Table(int[] array_table, int last_move, String str_table){
            this.last_move=last_move;
            this.str_table=str_table;
            table = new int[16];
            int i=0; //index for the array positions
            for(int j=0;j<4;j++){
                for(int k=0;k<4;k++){
                    if(array_table[i]==0){this.row=j;this.col=k;}
                    table[i]=array_table[i];
                    i++;
                }
            }
        }
    //Getters
        public int getBlankRow(){return this.row;}
        public int getBlankCol(){return this.col;}
        public int getLastMove(){return this.last_move;}
        public String getString(){return this.str_table;}
        public int[][] getTable(){ //returns a coppy table
            int[][] tmp=new int[4][4];
            int k=0;
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    tmp[i][j]=table[k];
                    k++;
                }
            }
            return tmp;
        }/*
        //instead of using a table we use an array, saves more memory;
        private String str_table="";
        private int row,col; //blank Position
        private int last_move;
            //0 ->initial
            //1 -1 -> Up Down
            //2 -2 -> Left Right
        //Constructor
            public Table(int last_move, String str_table){
                this.last_move=last_move;
                this.str_table=str_table;
                String[] s = str_table.split(" ");
                int i=0; //index for the array positions
                for(int j=0;j<4;j++){
                    for(int k=0;k<4;k++){
                        if(s[i].equals("0")){this.row=j;this.col=k;}
                        i++;
                    }
                }
            }
        //Getters
            public int getBlankRow(){return this.row;}
            public int getBlankCol(){return this.col;}
            public int getLastMove(){return this.last_move;}
            public String getString(){return this.str_table;}
            public int[][] getTable(){ //returns a coppy table
                int[][] tmp=new int[4][4];
                String[] s=this.str_table.split(" ");
                int k=0;
                for(int i=0;i<4;i++){
                    for(int j=0;j<4;j++){
                        tmp[i][j]=Integer.parseInt(s[k]);
                        k++;
                    }
                }
                return tmp;
            }*/
}
