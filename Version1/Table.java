import java.util.LinkedList;
class Table{
    //instead of using a table we use an array, saves more memory;
    private String str_table="";
    private int row,col; //blank Position
    private int[][] table;
    private int last_move;
        //0 ->initial
        //1 -1 -> Up Down
        //2 -2 -> Left Right
    //Constructor
        public Table(int[][] table,int last_move){
            this.last_move=last_move;
            this.table=new int[4][4];
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    if(table[i][j]==0){this.row=i;this.col=j;}
                    this.table[i][j]=table[i][j];
                    this.str_table+=table[i][j];
                }
            }
        }
        public Table(int[] array_table, int last_move, String str_table){
            this.last_move=last_move;
            this.str_table=str_table;
            table = new int[4][4];
            int i=0; //index for the array positions
            for(int j=0;j<4;j++){
                for(int k=0;k<4;k++){
                    if(array_table[i]==0){this.row=j;this.col=k;}
                    table[j][k]=array_table[i];
                    i++;
                }
            }
        }
    //Getters
        private boolean isValid(int r,int c){return (r<4 && r>=0) && (c<4 && c>=0);}
        public int getBlankRow(){return this.row;}
        public int getBlankCol(){return this.col;}
        public int getLastMove(){return this.last_move;}
        public String getString(){return this.str_table;}
        public int[][] getTable(){return this.table;}
        public LinkedList<Table> getDescedents(){
            int r=this.row,c=this.col;
            LinkedList<Table> descedents = new LinkedList<>();
            Table t;
            for(int i=-2;i<3;i++){
                if(i == -1*this.last_move || i==0) continue; //Simetric movement, not legal
                switch(i){
                    case 1: //Move Blank Up (r-1)(c==)
                        if(!isValid(r-1,c)) continue;
                        this.table[r][c]=this.table[r-1][c];
                        this.table[r-1][c]=0;
                        t = new Table(this.table,i);
                        descedents.add(t);
                        this.table[r-1][c]=this.table[r][c];
                        this.table[r][c]=0;
                    break;
                    case -1: //Move Blank Down (r+1)(c==)
                        if(!isValid(r+1,c)) continue;
                        this.table[r][c]=this.table[r+1][c];
                        this.table[r+1][c]=0;
                        t = new Table(this.table,i);
                        descedents.add(t);
                        this.table[r+1][c]=this.table[r][c];
                        this.table[r][c]=0;
                    break;
                    case 2: //Move Blank Left (r==)(c-1)
                        if(!isValid(r,c-1)) continue;
                        this.table[r][c]=this.table[r][c-1];
                        this.table[r][c-1]=0;
                        t= new Table(this.table,i);
                        descedents.add(t);
                        this.table[r][c-1]=this.table[r][c];
                        this.table[r][c]=0;
                    break;
                    case -2: //Move Blank Right (r==)(c+1)
                        if(!isValid(r,c+1)) continue;
                        this.table[r][c]=this.table[r][c+1];
                        this.table[r][c+1]=0;
                        t = new Table(this.table,i);
                        descedents.add(t);
                        this.table[r][c+1]=this.table[r][c];
                        this.table[r][c]=0;
                    break;
                }
            }
            return descedents;
        }
}
