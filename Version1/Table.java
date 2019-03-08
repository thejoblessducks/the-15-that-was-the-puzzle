import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
class Table{
    //Class to store a configuration of the board
    /*
        Contains 4 values:
            -> The string configuration of the board (str_table)
            -> The row of the blank cell in board
            -> The collum of the blank cell in board
            -> the matrix representing the board
            -> The last move (Left->-2/Up->1/Down->-1/Right->2)used to reach this configuration
        Operations;
            -> 2 Constructors, one giving a matrix, and one giving an array
            -> Check if move to be performed is valid
            -> Get the row where blank cell lies
            -> Get collum where blank cell lies
            -> Get the last move to reach this configuration
            -> Get the string configuration of this board
            -> Get a copy of this board's matrix
            -> Get a LinkedList of all the configuration generated from this one using operators
    */
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
            //Possible Implementation if we want to randomly generate the moves
                /*HashSet<Integer> set = new HashSet<>();
                Random rand = new Random();
                int j;
                while(set.size()!=4){
                    j=rand.nextInt((2-(-2)+1))+(-2);
                    set.add(j);
                }*/
            Table t;
            for(int i=-2;i<3;i++)/*: set)*/{
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
