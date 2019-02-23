import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Hashtable;

public class PuzzleBFS{
    public static int[] temp_array= new int[16];
    public static int[][] temp_table;
    public static long start_time, end_time;
    public static String temp_str,solution_moves;
    public static String solution_str = "";


    public static boolean isGoal(String table){
        return solution_str.equals(table);
    }
    public static boolean isSolvable(int[] table, int row){//Finished
        int inversions = 0,tmp = 0;
        boolean bottom_even;

        /*if( row == 1 || row == 3) bottom_even = true;
        else bottom_even=false;*/
        bottom_even= !(row%2==0);

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
            case 0:  s+="PosInicial- "; break;
            case 1:  s+="Up "; break;
            case -1:  s+="Down "; break;
            case 2:  s+="Left "; break;
            case -2:  s+="Right "; break;
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
                k++;
            }
        }
        return;
    }

    public static Table stepToTake(Table t, int move,Hashtable<String,String[]> h){
        int r=t.getBlankRow(),c=t.getBlankCol();
        temp_table=t.getTable();
        switch(move){
            case 1: //Move Blank Up (r-1)(c==)
                if(!isValid(r-1,c)) return null;
                temp_table[r][c]=temp_table[r-1][c];
                temp_table[r-1][c]=0;
                r--;
            break;
            case -1: //Move Blank Down (r+1)(c==)
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
            case -2: //Move Blank Right (r==)(c+1)
                if(!isValid(r,c+1)) return null;
                temp_table[r][c]=temp_table[r][c+1];
                temp_table[r][c+1]=0;
            break;
        }
        makeArray(temp_table);
        if(isGoal(temp_str)){
            end_time = System.nanoTime();
            solution_moves=putPath(h.get(t.getString())[0],move); //makes the final set of moves
            solutionFound(t.getString(),h);
            System.exit(0);
        }
        if(h.containsKey(temp_str)) return null;
        if(!isSolvable(temp_array,r)) return null;
        Table ts = new Table(temp_array,move,temp_str); //new node created
        return ts;
    }
    public static String[] makePath(String moves,String steps){
        //[0]-sequence of moves
        //[1]- number of steps taken
        String[] path = new String[2];
        path[0]=moves;
        path[1]=Integer.parseInt(steps)+1+"";
        return path;
    }
    public static void tableBFS(Table initial){
        Hashtable<String,String[]> h = new Hashtable<>(); //Marker
        Queue<Table> q = new LinkedList<>();
        String tmp;
        Table t,son;

        tmp=putPath("",initial.getLastMove());
        h.put(initial.getString(),makePath(tmp,"-1"));
        q.add(initial);

        while(!q.isEmpty()){
            t=q.remove();
            for(int i= -2; i<3; i++){
                if(i == -1*t.getLastMove() || i==0) continue; //Simetric movement, not legal
                son= stepToTake(t,i,h);
                if(!(son == null)){
                    //if null, is invalid movement, or not solvable or already existing
                    tmp=putPath(h.get(t.getString())[0], son.getLastMove());
                    h.put(son.getString(),makePath(tmp,h.get(t.getString())[1]));
                    q.add(son);
                }
            }
        }
        return;
    }
    public static void solutionFound(String parent,Hashtable<String,String[]> h){
        int steps = Integer.parseInt(h.get(parent)[1]) +1;
        long total_time= end_time - start_time;
        double seconds = (double)total_time/ 1_000_000_000.0;
        System.out.println("Solução encontrada:");
        System.out.println("Número de movimentos: "+steps);
        System.out.println("Movimentos:\n "+solution_moves);
        System.out.println("Número de nós criados: "+h.size());
        System.out.println("Tempo total BFS: "+seconds+" segundos");

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
                    str += tmp;
                    if(tmp == 0) blank_row = i;
                    k++;
                }
            }
        //Scan Final State Table
            for(int i = 0; i<16; i++){
                tmp = in.nextInt();
                solution_str +=tmp;
            }
        //Tests
        start_time = System.nanoTime();
        if(isGoal(str)){
            System.out.println("Solução óptima, tabelas iguais");
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
