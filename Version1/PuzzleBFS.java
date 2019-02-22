import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Hashtable;

public class PuzzleBFS{
    public static int[] temp_array= new int[16];
    public static int[][] temp_table;
    public static String temp_str;
    public static String solution_str = "";

    public static boolean isGoal(String table){
        return solution_str.equals(table);
    }
    public static boolean isSolvable(int[] table, int row){//Finished
        int inversions = 0,tmp = 0;
        boolean bottom_even;

        if( row == 1 || row == 3) bottom_even = true;
        else bottom_even=false;

        for(int i=0; i<16; i++){
            if(table[i]==0) continue;
            for(int j = i+1; j<16; j++){
                if(table[j] < table[i] && table[j] != 0)
                    inversions++;
            }
        }
        return (bottom_even) == (inversions %2 == 0);
    }
    public static boolean isValid(int r,int c){
        return (r<4 && r>=0) && (c<4 && c>=0);
    }
    public static String putPath(String s,int p){
        switch(p){
            case 0:  s+=" I"; break;
            case 1:  s+=" Up"; break;
            case -1:  s+=" Down"; break;
            case 2:  s+=" Left"; break;
            case -2:  s+=" Right"; break;
        }
        return s;
    }
    public static void makeArray(int[][] tb){
        temp_str="";
        int k=0;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                temp_array[k]=tb[i][j];
                temp_str+=temp_array[k];
            }
        }
        return;
    }

    public static Table stepToTake(Table t, int move,Hashtable<String,String[]> h){
        int r=t.getBlankRow(),c=t.getBlankCol();
        temp_table=t.getTable();
        switch(move){
            case -1: //Move Blank Up (r-1)(c==)
                if(!isValid(r-1,c)) return null;
                temp_table[r][c]=temp_table[r-1][c];
                temp_table[r-1][c]=0;
                r--;
            break;
            case 1: //Move Blank Down (r+1)(c==)
                if(!isValid(r+1,c)) return null;
                temp_table[r][c]=temp_table[r+1][c];
                temp_table[r+1][c]=0;
                r++;
            break;
            case 2: //Move Blank Left (r==)(c-1)
                if(!isValid(r,c-1)) return null;
                temp_table[r][c]=temp_table[r][c-1];
                temp_table[r][c-1]=0;
            break;
            case 3: //Move Blank Right (r==)(c+1)
                if(!isValid(r,c+1)) return null;
                temp_table[r][c]=temp_table[r][c+1];
                temp_table[r][c+1]=0;
            break;
        }
        makeArray(temp_table);
        if(isGoal(temp_str)){ System.out.println("Goal finish"); System.exit(0);}
        if(h.containsKey(temp_str)) return null;
        if(!isSolvable(temp_array,r)) return null;
        Table ts = new Table(temp_array,move,temp_str); //new node created
        return ts;
    }
    public static void tableBFS(Table initial){
        Hashtable<String,String[]> h = new Hashtable<>(); //Marker
        Queue<Table> q = new LinkedList<>();
        String[] path = new String[2];
        Table t,son;

        path[0]=putPath("",initial.getLastMove());
        path[1]="Initial";
        h.put(initial.getString(),path);
        q.add(initial);
        while(!q.isEmpty()){
            t=q.remove();
            if (isGoal(t.getString())){System.out.println("Finish"); return;}
            for(int i= -1; i<3; i++){
                if(i == -1*t.getLastMove()) continue; //Simetric movement, not legal
                son= stepToTake(t,i,h);
                if(!(son == null)){
                    //if null, is invalid movement, or not solvable or already existing
                    path[0]=putPath(h.get(t.getString())[0], son.getLastMove());
                    path[1]=t.getString();
                    q.add(son);
                }
            }
        }
        return;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String str = "";
        int tmp, blank_row=0;

        //Scan Initial State Table
            int k = 0;
            for(int i = 0; i<4; i++){
                for(int j = 0; j<4; j++){
                    tmp = in.nextInt();
                    temp_array[k] = tmp;
                    str += tmp + " ";
                    if(tmp == 0) blank_row = i;
                    k++;
                }
            }
        //Scan Final State Table
            for(int i = 0; i<16; i++){
                tmp = in.nextInt();
                solution_str += tmp + " ";
            }
        //Tests
        if(isGoal(str)){
            //Open function to return:
                //the number of moves to reach solution
                //the sequence of moves
                //the time consumed
                //the number of nodes created
                //the number of nodes used/in Hashtable
        }
        else{
            if(isSolvable(temp_array,blank_row)){
                Table init = new Table(temp_array,0,str);
                tableBFS(init);
            }
            else{
                System.out.println("Solution not Found");
                System.exit(0);
            }
        }
    }
}
