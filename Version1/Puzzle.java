import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.HashMap;
import java.util.Comparator;
import java.util.PriorityQueue;

class PriorityNodes implements Comparable<PriorityNodes>{
    /*Class that creates a node to enter in a PriorityQueue
    This class is composed by two values, the class Table and its heuristic distance
    Heuristic distance can be either Manhattan distance or Hamming distance
    */
    Table t;
    int heuristic_dist;

    PriorityNodes(Table t, int heuristic_dist){
        this.t=t; this.heuristic_dist=heuristic_dist;
    }
    public Table getTable(){return this.t;}
    public int getDist(){return this.heuristic_dist;}
    public int compareTo(PriorityNodes pn){
        //Implementation of comparator to use with PQueue
        if(this.getDist()==pn.getDist()) return 0;
        if(this.getDist()>pn.getDist()) return 1;
        return -1;
    }
}
/*------------------------------------------------------------------------------
15 Puzzle main Programm
------------------------------------------------------------------------------*/
public class Puzzle{
    public static int[][] final_state=new int[4][4];
    public static long start_time, end_time;
    public static String solution_str="";


/*------------------------------------------------------------------------------
Auxiliar Functions: isGoalState/isSolvable/isValidMove/putPath/makePath
------------------------------------------------------------------------------*/
    public static boolean isGoal(String table){
        return solution_str.equals(table);
    }
    public static boolean isSolvable(int[] table, int row){
        /*An even table is solvable if:
            a)the blank_row from bottom is even and #inversions is even
            b)the blank_row from bottom is odd and #inversions is odd
            In general, (bottom_even) == (inversions %2 == 0)
        */
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
    public static boolean isValid(int r,int c){
        //Checks if the row and column are inside the table matrix
        return (r<4 && r>=0) && (c<4 && c>=0);
    }
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
//------------------------------------------------------------------------------


/*------------------------------------------------------------------------------
Heuristic Functions: Hamming distance and Manhattan distance
------------------------------------------------------------------------------*/
    public static int distHamming(Table tb){
        //Calculates the total number of displaced cells
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
        int manh=0;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(!(table[i][j]==final_state[i][j]) && table[i][j]!=0){
                    outerloop:
                        for(int r=0;r<4;r++){
                            for(int c=0;c<4;c++){
                                if(table[i][j]==final_state[r][c]){
                                    manh+=Math.abs(r-i)+Math.abs(c-j);
                                    break outerloop;
                                }
                            }
                        }
                }
            }
        }
        return manh;
    }
//------------------------------------------------------------------------------


/*------------------------------------------------------------------------------
Search Methods: BFS/LDFS/IDFS/A*(Hamming/Manhattan)/Greedy(Hamming/Manhattan)
------------------------------------------------------------------------------*/
    public static void tableBFS(Table initial){
        HashMap<String,String[]> h = new HashMap<>(); //Marker for visited nodes
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
                    int steps = Integer.parseInt(h.get(t.getString())[1]);
                    solutionFound(steps,h,ts);
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
        return;
    }

    public static void dfsVisit(Table t,HashMap<String,String[]> h,int limit){
        if(isGoal(t.getString())){
            end_time = System.nanoTime();
            solutionFound(Integer.parseInt(h.get(t.getString())[1])-1,h,h.get(t.getString())[0]);
        }
        if(Integer.parseInt(h.get(t.getString())[1])==limit){
            h.remove(t.getString()); return;
        }
        for(Table son : t.getDescedents()){
            // System.out.println(son.getString());
            if(!(h.containsKey(son.getString()))){
                String ts=putPath(h.get(t.getString())[0], son.getLastMove()); //makes a move
                h.put(son.getString(),makePath(ts,h.get(t.getString())[1])); //updates de sequence and puts in_stack
                dfsVisit(son,h,limit);
            }
        }
        h.remove(t.getString());
        return;
    }
    public static void tableDFS2(Table tb,int limit){
        HashMap<String,String[]> h = new HashMap<>();
        String ts = "PosInicial- "; //saves the sequence of moves for each node
        h.put(tb.getString(),makePath(ts,"-1"));

        for(Table t : tb.getDescedents()){
            System.out.println(t.getString());
            ts=putPath(h.get(tb.getString())[0], t.getLastMove()); //makes a move
            h.put(t.getString(),makePath(ts,h.get(tb.getString())[1])); //updates de sequence and puts in_stack
            dfsVisit(t,h,limit);
        }
    }
    public static void tableDFS1(Table tb,int limit){//For IDFS
        //Without limit, method never ends-->OutOfMemmory
        //Hence, we already adapt the method to accommodate the IDFS implememtation
        HashMap<String,String[]> h = new HashMap<>(); //Marker for visited elements
        HashMap<String,String[]> in_stack = new HashMap<>(); //Marker for nodes in Stack
        Stack<Table> stack = new Stack<>();
        Table t;

        String ts = "PosInicial- "; //saves the sequence of moves for each node
        stack.push(tb);
        in_stack.put(tb.getString(),makePath(ts,"-1"));
        while(!stack.isEmpty()){
            t = stack.pop();
            h.put(t.getString(),in_stack.get(t.getString()));
            in_stack.remove(t.getString()); //visited and in Stack

            if(isGoal(t.getString())){
                end_time = System.nanoTime();
                solutionFound(Integer.parseInt(h.get(t.getString())[1]),h,h.get(t.getString())[0]);
            }
            //Depth and Limit Checker
            if(Integer.parseInt(h.get(t.getString())[1])==limit){
                h.remove(t.getString()); continue;
            }

            for(Table son : t.getDescedents()){
                if(!(h.containsKey(son.getString())) && !(in_stack.containsKey(son.getString()))){
                    ts=putPath(h.get(t.getString())[0], son.getLastMove()); //makes a move
                    in_stack.put(son.getString(),makePath(ts,h.get(t.getString())[1])); //updates de sequence and puts in_stack
                    stack.push(son);
                }
            }
        }
        h.clear();
        in_stack.clear();
        stack.clear();
        return;
    }
    public static void tableIDFS(Table tb){
        //In theory we can reach the standart state in at least 80 moves, so that is our final limit
        for(int i=0;i<=80;i++)
            tableDFS1(tb,i);
        return;
    }
    /*A*, uses a PQ to save the nodes, it uses as a weight for the PQ a function
      f(n)=g(n)+h(n):
       g(n) is the step
       h(n) is the heuristic distance-- doesn't underestimate the value
      A* guarantees to always reach optimal solution*/
    public static void tableAHamming(Table tb){
        //A* implememtation using Hamming heuristic
        PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
        HashMap<String,String[]> h = new HashMap<>();
        PriorityNodes t;

        t = new PriorityNodes(tb,distHamming(tb));
        pq.add(t);

        String ts = putPath("",tb.getLastMove());
        h.put(tb.getString(),makePath(ts,"-1"));
        while(!pq.isEmpty()){
            t = pq.remove();
            if(isGoal(t.getTable().getString())){
                end_time = System.nanoTime();
                int steps = Integer.parseInt(h.get(t.getTable().getString())[1]);
                solutionFound(steps,h,h.get(t.getTable().getString())[0]);
            }
            for(Table son : t.getTable().getDescedents()){
                if(!(h.containsKey(son.getString()))){
                    ts = putPath(h.get(t.getTable().getString())[0], son.getLastMove());
                    h.put(son.getString(),makePath(ts,h.get(t.getTable().getString())[1]));

                    int steps = Integer.parseInt(h.get(son.getString())[1]);
                    t = new PriorityNodes(son,steps+distHamming(son));
                    pq.add(t);
                }
            }
        }return;
    }
    public static void tableAManhattan(Table tb){
        //A* implememtation using Manhattan heuristic
        PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
        HashMap<String,String[]> h = new HashMap<>();
        PriorityNodes t;

        t = new PriorityNodes(tb,distManhattan(tb));
        pq.add(t);

        String ts = putPath("",tb.getLastMove());
        h.put(tb.getString(),makePath(ts,"-1"));
        while(!pq.isEmpty()){
            t = pq.remove();
            if(isGoal(t.getTable().getString())){
                end_time = System.nanoTime();
                int steps = Integer.parseInt(h.get(t.getTable().getString())[1]);
                solutionFound(steps,h,h.get(t.getTable().getString())[0]);
            }
            for(Table son : t.getTable().getDescedents()){
                if(!(h.containsKey(son.getString()))){
                    ts = putPath(h.get(t.getTable().getString())[0], son.getLastMove());
                    h.put(son.getString(),makePath(ts,h.get(t.getTable().getString())[1]));

                    int steps = Integer.parseInt(h.get(son.getString())[1]);
                    t = new PriorityNodes(son,steps+distManhattan(son));
                    pq.add(t);
                }
            }
        }return;
    }
    public static void tableGreedyHamming(Table tb){
        //The greedy works almost as the A*, except, f(n)=h(n)
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
                int steps =Integer.parseInt(h.get(t.getTable().getString())[1]);
                solutionFound(steps,h,h.get(t.getTable().getString())[0]);
            }
            for(Table son : t.getTable().getDescedents()){
                if(!(h.containsKey(son.getString()))){
                    ts=putPath(h.get(t.getTable().getString())[0], son.getLastMove());
                    h.put(son.getString(),makePath(ts,h.get(t.getTable().getString())[1]));
                    t = new PriorityNodes(son,distHamming(son));
                    pq.add(t);
                }
            }
        }return;
    }
    public static void tableGreedyManhattan(Table tb){
        //Greedy implememtation using Manhattan heuristic
        PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
        HashMap<String,String[]> h = new HashMap<>();
        PriorityNodes t;

        t = new PriorityNodes(tb,distManhattan(tb));
        pq.add(t);

        String ts = putPath("",tb.getLastMove());
        h.put(tb.getString(),makePath(ts,"-1"));
        while(!pq.isEmpty()){
            t = pq.remove();
            if(isGoal(t.getTable().getString())){
                end_time = System.nanoTime();
                int steps = Integer.parseInt(h.get(t.getTable().getString())[1]);
                solutionFound(steps,h,h.get(t.getTable().getString())[0]);
            }
            for(Table son : t.getTable().getDescedents()){
                if(!(h.containsKey(son.getString()))){
                    ts = putPath(h.get(t.getTable().getString())[0], son.getLastMove());
                    h.put(son.getString(),makePath(ts,h.get(t.getTable().getString())[1]));

                    t = new PriorityNodes(son,distManhattan(son));
                    pq.add(t);
                }
            }
        }return;
    }
//------------------------------------------------------------------------------


/*------------------------------------------------------------------------------
Soluction Function (will terminate all the programm)
------------------------------------------------------------------------------*/
    public static void solutionFound(int t_steps,HashMap<String,String[]> h,String moves){
        int steps = t_steps;
        long total_time= end_time - start_time;
        double seconds = (double)total_time/ 1_000_000_000.0;
        System.out.println("Solução encontrada:");
        System.out.println("Número de movimentos: "+steps);
        System.out.println("Movimentos:\n "+moves);
        System.out.println("Número de nós criados: "+h.size());
        System.out.println("Tempo total: "+seconds+" segundos");
        System.exit(0);
    }
//------------------------------------------------------------------------------


/*------------------------------------------------------------------------------
Main Reader and Choice Functions
------------------------------------------------------------------------------*/
    public static Table readGivenTable(Scanner stdin){
        int initial_blank_row = 0,final_blank_row = 0,tmp,k = 0;
        int[] initial_a,final_a;
        String initial_str = "";
        initial_a = new int[16];
        final_a = new int[16];

        //Scan Initial State Table
        for(int i = 0; i<4; i++){
            for(int j = 0; j<4; j++){
                tmp = stdin.nextInt();
                if(tmp == 0) initial_blank_row = i;
                initial_a[k] = tmp;
                initial_str += tmp;
                k++;
            }
        }
        //Scan Final State Table
        k=0;
        for(int i = 0; i<4; i++){
            for(int j=0;j<4;j++){
                tmp = stdin.nextInt();
                if(tmp == 0) final_blank_row = i;
                solution_str += tmp;
                final_a[k] = tmp;
                final_state[i][j] = tmp;
                k++;
            }
        }
        if(isGoal(initial_str)){
            System.out.println("Solução óptima, tabelas iguais");
            System.exit(0);
        }
        if(!(isSolvable(initial_a,initial_blank_row) == isSolvable(final_a,final_blank_row))){
            //Both states are not in equal condition
            System.out.println("Não tem solução");
            System.exit(0);
        }
        Table init = new Table(initial_a,0,initial_str);
        return init;
    }
    public static void makeChoice(Scanner stdin, Table init){
        int tmp; double sec;
        System.out.println("Escolha o método:");
        System.out.print("1)BFS\n2)LDFS\n3)IDFS\n4)A* heurística Hamming  5)A* heurística Manhattan\n6)Greedy heurística Hamming  7)Greedy heurística Manhattan");
        tmp = stdin.nextInt(); System.out.println("\n");
        switch(tmp){
            case 1://BFS
                System.out.println("\nBFS:");
                start_time=System.nanoTime();
                tableBFS(init);
            break;
            case 2://LDFS
                System.out.println("LDFS:");
                //System.out.println("O DFS sem limite de ciclo não termina :(");
                start_time=System.nanoTime();
                tableDFS2(init,25);
                System.out.println("Solução não encontrada:");
                sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
                System.out.println("Tempo: "+sec+" segundos");
            break;
            case 3://IDFS
                System.out.println("IDFS:");
                start_time=System.nanoTime();
                tableIDFS(init);
            break;
            case 4://A* H
                System.out.println("A* heurística Hamming:");
                start_time=System.nanoTime();
                tableAHamming(init);
                System.out.println("Solução não encontrada:");
                sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
                System.out.println("Tempo: "+sec+" segundos");
            break;
            case 5://A* Man
                System.out.println("A* heurística Manhattan:");
                start_time=System.nanoTime();
                tableAManhattan(init);

                System.out.println("Solução não encontrada:");
                sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
                System.out.println("Tempo: "+sec+" segundos");
            break;
            case 6://Gr Ham
                System.out.println("Greedy heurística Hamming");
                start_time=System.nanoTime();
                tableGreedyHamming(init);

                System.out.println("Solução não encontrada:");
                sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
                System.out.println("Tempo: "+sec+" segundos");
            break;
            case 7://Gr Man
                System.out.println("Greedy heurística Manhattan");
                start_time=System.nanoTime();
                tableGreedyManhattan(init);

                System.out.println("Solução não encontrada:");
                sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
                System.out.println("Tempo: "+sec+" segundos");
            break;
            default: System.out.println("Opção errada :("); break;
        }
    }
//------------------------------------------------------------------------------


/*------------------------------------------------------------------------------
Main Function
------------------------------------------------------------------------------*/
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int tmp;
        Table init= readGivenTable(in);
        makeChoice(in,init);
        return;
    }
}
