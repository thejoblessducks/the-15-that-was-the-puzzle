import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.HashMap;
import java.util.Comparator;
import java.util.PriorityQueue;

class PriorityNodes implements Comparable<PriorityNodes>{
    Table t;
    int heuristic_dist;
    PriorityNodes(Table t, int heuristic_dist){
        this.t=t; this.heuristic_dist=heuristic_dist;
    }
    public Table getTable(){return this.t;}
    public int getDist(){return this.heuristic_dist;}
    public int compareTo(PriorityNodes pn){
        if(this.getDist()==pn.getDist())
            return 0;
        if(this.getDist()>pn.getDist())
            return 1;
        return -1;
    }
}

public class Puzzle{
    public static int[] temp_array= new int[16];
    public static int[][] final_state=new int[4][4];
    public static long start_time, end_time;
    public static String solution_str="";

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
    public static int distHamming(Table tb){
        int diffs=0;
        int[][] table=tb.getTable();
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(table[i][j] != final_state[i][j])
                    diffs++;
            }
        }
        return diffs;
    }
    public static int distManhattan(Table tb){
        int[][] table=tb.getTable();
        int r,c,manh=0;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(!(table[i][j]==final_state[i][j])){
                    outerloop:
                        for(r=0;r<4;r++){
                            for(c=0;c<4;c++){
                                if(table[i][j]==final_state[i][j]){
                                    manh+=Math.abs(i-r)+Math.abs(j-c);
                                    break outerloop;
                                }
                            }
                        }
                }
            }
        }
        return manh;
    }
    //Search Methods
        public static void tableGreedy(Table tb){}
            
        public static void tableAHamming(Table tb){
            //Hamming heuristic
            PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
            HashMap<String,String[]> h = new HashMap<>();
            PriorityNodes t;
            t= new PriorityNodes(tb,distHamming(tb));
            String ts=putPath("",tb.getLastMove());
            h.put(tb.getString(),makePath(ts,"-1"));
            pq.add(t);
            while(!pq.isEmpty()){
                t=pq.remove();
                if(isGoal(t.getTable().getString())){
                    end_time = System.nanoTime();
                    solutionFound(Integer.parseInt(h.get(t.getTable().getString())[1])-1,h,h.get(t.getTable().getString())[0]);
                }
                for(Table son : t.getTable().getDescedents()){
                    if(!(h.containsKey(son.getString()))){
                        ts=putPath(h.get(t.getTable().getString())[0], son.getLastMove());
                        h.put(son.getString(),makePath(ts,h.get(t.getTable().getString())[1]));
                        t = new PriorityNodes(son,distHamming(son));
                        pq.add(t);
                    }
                }
            }
        }
        public static void tableAManhattan(Table tb){
            PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
            HashMap<String,String[]> h = new HashMap<>();
            PriorityNodes t;
            t= new PriorityNodes(tb,distManhattan(tb));
            String ts=putPath("",tb.getLastMove());
            h.put(tb.getString(),makePath(ts,"-1"));
            pq.add(t);
            while(!pq.isEmpty()){
                t=pq.remove();
                if(isGoal(t.getTable().getString())){
                    end_time = System.nanoTime();
                    solutionFound(Integer.parseInt(h.get(t.getTable().getString())[1])-1,h,h.get(t.getTable().getString())[0]);
                }
                for(Table son : t.getTable().getDescedents()){
                    if(!(h.containsKey(son.getString()))){
                        ts=putPath(h.get(t.getTable().getString())[0], son.getLastMove());
                        h.put(son.getString(),makePath(ts,h.get(t.getTable().getString())[1]));
                        t = new PriorityNodes(son,distManhattan(son));
                        pq.add(t);
                    }
                }
            }
        }

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
            HashMap<String,String[]> in_stack = new HashMap<>(); //Marker for nodes in Stack
            Stack<Table> stack = new Stack<>();
            Table t;

            String ts="PosInicial- "; //saves the sequence of moves for each node
            stack.push(tb);
            in_stack.put(tb.getString(),makePath(ts,"-1"));
            while(!stack.isEmpty()){
                t=stack.pop();
                h.put(t.getString(),in_stack.get(t.getString()));
                in_stack.remove(t.getString()); //visited and in Stack
                if(isGoal(t.getString())){
                    end_time = System.nanoTime();
                    solutionFound(Integer.parseInt(h.get(t.getString())[1])-1,h,h.get(t.getString())[0]);
                }
                //Depth and Limit Checker
                    if(Integer.parseInt(h.get(t.getString())[1])==limit) continue;
                for(Table son : t.getDescedents()){
                    if(!(h.containsKey(son.getString())) && !(in_stack.containsKey(son.getString()))){
                        ts=putPath(h.get(t.getString())[0], son.getLastMove());
                        in_stack.put(son.getString(),makePath(ts,h.get(t.getString())[1]));
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
            Table t;
            String ts=putPath("",initial.getLastMove());
            h.put(initial.getString(),makePath(ts,"-1"));
            q.add(initial);
            while(!q.isEmpty()){
                t=q.remove();
                for(Table son : t.getDescedents()){
                    if(isGoal(son.getString())){
                        end_time = System.nanoTime();
                        ts=putPath(h.get(t.getString())[0],son.getLastMove()); //makes the final set of moves
                        solutionFound(Integer.parseInt(h.get(t.getString())[1]),h,ts);
                    }
                    if(!(h.containsKey(son.getString()))){
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
        public static void solutionFound(int t_steps,HashMap<String,String[]> h,String moves){
            int steps = t_steps +1;
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
                    if(tmp == 0) blank_row=i;
                    temp_array[k]=tmp;
                    str+=tmp;
                    k++;
                }
            }
        //Scan Final State Table
            k=0;
            for(int i = 0; i<4; i++){
                for(int j=0;j<4;j++){
                    tmp = in.nextInt();
                    if(tmp == 0) br_f = i;
                    solution_str+=tmp;
                    f[k]=tmp;
                    final_state[i][j]=tmp;
                    k++;
                }
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
                    //System.out.println("O DFS sem limite de ciclo não termina :(");
                    start_time=System.nanoTime();
                    tableDFS(init,27);
                    System.out.println("Solução não encontrada:");
                    double sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
                    System.out.println("Tempo: "+sec+" segundos");
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
