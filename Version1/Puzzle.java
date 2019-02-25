import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.HashMap;

public class Puzzle{
    public static int[] temp_array= new int[16];
    public static long start_time, end_time;
    public static String solution_str = "";

    //Checkers (isGoalState/isSolvable/isValidMove)
        public static boolean isGoal(String table){return solution_str.equals(table);}
        public static boolean isSolvable(int[] table, int row){
            int inversions = 0;
            boolean bottom_even=!(row%2==0);
            for(int i=0; i<16; i++){
                if(table[i]==0) continue;
                for(int j = i+1; j<16; j++){
                    if(table[j] < table[i] && table[j] != 0)
                        inversions++;
                }
            }return (bottom_even) == (inversions %2 == 0);
        }
        public static boolean isValid(int r,int c){return (r<4 && r>=0) && (c<4 && c>=0);}
    //Makers(addMove/createPath/makeArray)
        public static String putPath(String s,int p){
            switch(p){
                case 0:  s+="PosInicial- "; break;
                case 1:  s+="Up "; break;
                case -1:  s+="Down "; break;
                case 2:  s+="Left "; break;
                case -2:  s+="Right "; break;
            }return s;
        }
        public static String[] makePath(String moves,String steps){
            //[0]-sequence of moves
            //[1]- number of steps taken
            String[] path = new String[2];
            path[0]=moves;
            path[1]=Integer.parseInt(steps)+1+"";
            return path;
        }
        public static String makeArray(int[][] tb){
            String t="";
            int k=0;
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    temp_array[k]=tb[i][j];
                    t+=temp_array[k];
                    k++;
                }
            }return t;
        }
    //Heuristics for A* and Greedy

    //Maker Table General(Makes a general verification, appliable for BFS/DFS/IDFS/A*/Greedy)
        public static Table stepToTake(Table t, int move,HashMap<String,String[]> h){
            //2-DFS
            int r=t.getBlankRow(),c=t.getBlankCol();
            int[][] temp_table=t.getTable();
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
            String ts = makeArray(temp_table);
            if(isGoal(ts)){
                end_time = System.nanoTime();
                ts=putPath(h.get(t.getString())[0],move); //makes the final set of moves
                solutionFound(t.getString(),h,ts);
            }
            if(h.containsKey(t)){
                /*if(method != 2) // for all methods but DFS/IDFS
                    return null;
                int in_hash_steps = Integer.parseInt(h.get(t.getString())[1]);//get number of steps
                if(current_steps < in_hash_steps){
                    //takes less steps to reach this configuration, when compared to the already existing one
                    //the one in Hash will be removed
                    h.remove(t.getString());
                    Table tb = new Table(temp_array,move,ts);
                    return tb;
                }*/
                return null;
            }
            Table tb = new Table(temp_array,move,ts); //new node created
            temp_table=null;
            return tb;
        }
    //Search Methods
        public static void tableIDFS(Table tb){
            //In theory we can reach the standart state in at least 80 moves, so that is our final limit
            for(int i=0;i<=80;i++)
                tableDFS(tb,i);
            System.out.println("Solução não encontrada:");
            double sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
            System.out.println("Tempo: "+sec+" segundos");
            return;
        }

        public static void tableDFS(Table tb,int limit){
            //Without limit, method never ends-->OutOfMemmory
            //Hence, we already adapt the method to accommodate the IDFS implememtation

            HashMap<String,String[]> h = new HashMap<>(); //Marker for visited elements
            HashMap<String,String> in_stack = new HashMap<>(); //Marker for nodes in Stack
            Stack<Table> stack = new Stack<>();
            Table t,son;

            String ts="PosInicial- "; //saves the sequence of moves for each node
            h.put(tb.getString(),makePath(ts,"-1"));
            stack.push(tb);
            in_stack.put(tb.getString(),null);
            System.out.println("Limit:"+limit);
            while(!stack.isEmpty()){
                t=stack.pop();
                in_stack.remove(t.getString()); //visited and in Stack
                //Depth and Limit Checker
                    if(Integer.parseInt(h.get(t.getString())[1])>limit) continue;
                for(int i= -2; i<3; i++){
                    if(i == -1*t.getLastMove() || i==0) continue; //Simetric movement, not legal
                    son= stepToTake(t,i,h);
                    //if null, is invalid movement, or not solvable or already existing
                    if(!(son == null) && !in_stack.containsKey(son.getString())){
                        ts=putPath(h.get(t.getString())[0], son.getLastMove());
                        h.put(son.getString(),makePath(ts,h.get(t.getString())[1]));
                        in_stack.put(son.getString(),null);
                        stack.push(son);
                    }
                }
            }
            h.clear();
            in_stack.clear();
            stack.clear();
            return;
        }

        public static void tableBFS(Table initial){
            HashMap<String,String[]> h = new HashMap<>(); //Marker
            Queue<Table> q = new LinkedList<>();
            Table t,son;
            String ts=putPath("",initial.getLastMove());
            h.put(initial.getString(),makePath(ts,"-1"));
            q.add(initial);
            while(!q.isEmpty()){
                t=q.remove();
                for(int i= -2; i<3; i++){
                    if(i == -1*t.getLastMove() || i==0) continue; //Simetric movement, not legal
                    son= stepToTake(t,i,h);
                    if(!(son == null)){
                        //if null, is invalid movement, or not solvable or already existing
                        ts=putPath(h.get(t.getString())[0], son.getLastMove());
                        h.put(son.getString(),makePath(ts,h.get(t.getString())[1]));
                        q.add(son);
                    }
                }
            }
            System.out.println("Solução não encontrada:");
            System.out.println("Número de nós: "+h.size());
            double sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
            System.out.println("Tempo: "+sec+" segundos");
            h.clear();
            q.clear();
            return;
        }
    //----------------------------------

    //Only if Solution was Found
        public static void solutionFound(String parent,HashMap<String,String[]> h,String moves){
            int steps = Integer.parseInt(h.get(parent)[1]) +1;
            long total_time= end_time - start_time;
            double seconds = (double)total_time/ 1_000_000_000.0;
            System.out.println("Solução encontrada:");
            System.out.println("Número de movimentos: "+steps);
            System.out.println("Movimentos:\n "+moves);
            System.out.println("Número de nós criados: "+h.size());
            System.out.println("Tempo total: "+seconds+" segundos");
            System.exit(0);
        }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int[] f = new int[16];
        String str = "";
        int tmp, blank_row=0,br_f=0;

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
            k=0;
            for(int i = 0; i<16; i++){
                tmp = in.nextInt();
                solution_str +=tmp;
                if(tmp == 0) br_f = i;
                f[k]=tmp;
                k++;
            }
        //Tests
        start_time = System.nanoTime();
        if(isGoal(str)){
            System.out.println("Solução óptima, tabelas iguais");
            System.exit(0);
        }
        if(!(isSolvable(temp_array,blank_row) == isSolvable(f,br_f))){
            System.out.println("Não tem solução");
            System.exit(0);
        }
        //Method Choice
            System.out.println("Escolha o método:");
            System.out.print("1)BFS--2)DFS--3)IDFS--4)A*--5)Greedy");
            Table init = new Table(temp_array,0,str);
            tmp=in.nextInt(); System.out.println("\n");
            switch(tmp){
                case 1:
                    System.out.println("BFS:");
                    start_time=System.nanoTime();
                    tableBFS(init);
                break;
                case 2:
                    System.out.println("DFS:");
                    System.out.println("O DFS sem limite de ciclo não termina :(");
                break;
                case 3:
                    System.out.println("IDFS:");
                    start_time=System.nanoTime();
                    tableIDFS(init);
                break;
                case 4: break;
                case 5: break;
                default: System.out.println("Opção errada :("); break;
            }
    }
}
