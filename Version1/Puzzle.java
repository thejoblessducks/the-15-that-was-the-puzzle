import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.HashMap;
import java.util.Comparator;
import java.util.PriorityQueue;

class PriorityNodes implements Comparable<PriorityNodes>{
    /*Class that creates a node to enter in a PriorityQueue
      Has 4 values:
       -> A value of type Table representing the board configuration for this Node
       -> An heuristic distance for the current configuration
            (Heuristic distance can be either Manhattan distance or Hamming distance)
       -> The number of steps used to reach this configuration
       -> The sequence of moves to reach this configuration
    */
    Table t;
    int heuristic_dist,steps; //heuristic_dist=g+h
    String moves;

    PriorityNodes(Table t, int heuristic_dist,int steps, String moves){
        this.t=new Table(t.getTable(),t.getLastMove());
        this.heuristic_dist=heuristic_dist;
        this.steps=steps;
        this.moves=moves;
    }
    public Table getTable(){return this.t;}
    public int getDist(){return this.heuristic_dist;}
    public int getSteps(){return this.steps;}
    public String getMoves(){return this.moves;}
    public int compareTo(PriorityNodes pn){
        //Implementation of comparator to use with PQueue
        if(this.getDist()==pn.getDist()) return 0;
        if(this.getDist()>pn.getDist()) return 1;
        return -1;
    }
}

class HashNode{
    //Class to be used has value of HashMaps
    //Contains the moves and number of steps of the given key in HashMap
    String moves;
    int steps;
    HashNode(String moves,int steps){
        this.moves=moves;
        this.steps=steps;
    }
    public String getMoves(){return moves;}
    public int getSteps(){return steps;}
}
/*------------------------------------------------------------------------------
15 Puzzle main Programm
------------------------------------------------------------------------------*/
public class Puzzle{
    public static int[][] final_state=new int[4][4]; //table of goal state
    public static int size=0; //variable to keep track of hash size in dfs
    public static long start_time, end_time;
    public static String solution_str=""; //string configuration of goal state


/*------------------------------------------------------------------------------
Auxiliar Functions: isGoalState/isSolvable/putPath
------------------------------------------------------------------------------*/
    public static boolean isGoal(String table){
        //tests if the given table string configuration is equal to the goal configuration
        return table.equals(solution_str);
    }
    public static boolean isSolvable(int[] table, int row){
        /*An even table is solvable if:
            a)the blank_row from bottom is odd and #inversions is even
            b)the blank_row from bottom is odd and #inversions is even
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
    public static String putPath(String s,int p){
        //officially creates/chooses the move to make
        switch(p){
            case 0:  s+="PosInicial- "; break;
            case 1:  s+="Up "; break;
            case -1:  s+="Down "; break;
            case 2:  s+="Left "; break;
            case -2:  s+="Right "; break;
        }return s;
    }
//------------------------------------------------------------------------------


/*------------------------------------------------------------------------------
Heuristic Functions: Hamming distance and Manhattan distance
------------------------------------------------------------------------------*/
    public static int distHamming(Table tb){
        //Calculates the total number of missplaced cells
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
        //calculates the distance of a cell to its goal location in goal table
        //the distance is the (distance_in_row)+(distance_in_col)
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
        //Supported by queue, it will only expand level d, after expanding d-1
        HashMap<String,HashNode> h = new HashMap<>(); //Marker for visited nodes
        Queue<Table> q = new LinkedList<>();
        HashNode hn;
        Table t;
        String s;

        hn=new HashNode("Initial-",0); //initialize the queue
        h.put(initial.getString(),hn);
        q.add(initial);
        while(!q.isEmpty()){
            t=q.remove();
            if(isGoal(t.getString())){
                //can end
                end_time = System.nanoTime();
                solutionFoundUnInformed(h.get(t.getString()),h.size());
            }
            for(Table son : t.getDescedents()){
                if(!(h.containsKey(son.getString()))){//new configuration found
                    s=putPath(h.get(t.getString()).getMoves(),son.getLastMove());
                    hn = new HashNode(s,h.get(t.getString()).getSteps()+1);
                    h.put(son.getString(),hn);
                    q.add(son);
                }
            }
        }
    }
    public static void dfsVisit(Table t,HashMap<String,HashNode> h,int limit,String s,HashNode hn){
        if(h.get(t.getString()).getSteps()==limit) return; //has reached the limit
        for(Table son : t.getDescedents()){
            if(!(h.containsKey(son.getString()))){//new configuration
                s=putPath(h.get(t.getString()).getMoves(), son.getLastMove());
                hn=new HashNode(s,h.get(t.getString()).getSteps()+1);
                h.put(son.getString(),hn); //adds to hash

                size=Math.max(size,h.size()); //utdates maximum hash size
                if(isGoal(son.getString())){
                    end_time = System.nanoTime();
                    solutionFoundUnInformed(h.get(son.getString()),size);
                }
                dfsVisit(son,h,limit,s,hn);
                h.remove(son.getString()); //all its children failed, it fails too
            }
        }
        return;
    }
    public static void tableDFS(Table tb,int limit){
        HashMap<String,HashNode> h = new HashMap<>();
        HashNode hn;
        String s = "Inicial- "; //saves the sequence of moves for each node

        hn=new HashNode(s,0);
        h.put(tb.getString(),hn);
        size=Math.max(size,h.size());

        dfsVisit(tb,h,limit,s,hn);
        return;
    }
    public static void tableIDFS(Table tb){
        //In theory we can reach the standart state in at least 80 moves, so that is our final limit
        size=0;
        for(int i=1;i<=80;i++)
            tableDFS(tb,i);
        size=0;
        return;
    }
    /*A*, uses a PQ to save the nodes, it uses as a weight for the PQ a function
      f(n)=g(n)+h(n):
       g(n) is the step
       h(n) is the heuristic distance-- doesn't underestimate the value
      A* guarantees to always reach optimal solution
    */
    public static void tableAHamming(Table tb){
        PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
        HashMap<String,HashNode> closed = new HashMap<>();
        PriorityNodes pn,tmp_pn;
        HashNode hn;
        int f,g,h;
        String s;

        g=0;
        h=distHamming(tb);
        f=g+h;
        pn=new PriorityNodes(tb,f,g,"Initial-");
        pq.add(pn);

        while(!pq.isEmpty()){
            pn=pq.poll();
            // in_pq.remove(pn.getTable().getString());
            if(isGoal(pn.getTable().getString())){
                end_time = System.nanoTime();
                solutionFoundInformed(pn,pq.size());
            }
            hn= new HashNode(pn.getMoves(),pn.getSteps());
            closed.put(pn.getTable().getString(),hn);
            for(Table son : pn.getTable().getDescedents()){
                if(!closed.containsKey(son.getString())){
                    g=pn.getSteps()+1;
                    h=distHamming(son);
                    f=g+h;
                    s=putPath(pn.getMoves(),son.getLastMove());
                    tmp_pn = new PriorityNodes(son,f,g,s);
                    pq.add(tmp_pn);
                }
            }
        }
    }
    public static void tableAManhattan(Table tb){
        //A* implememtation using Manhattan heuristic
        PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
        HashMap<String,HashNode> closed = new HashMap<>();
        PriorityNodes pn,tmp;
        HashNode hn;
        int f,g,h;
        String s;
        g=0;
        h=distManhattan(tb);
        f=g+h;
        pn=new PriorityNodes(tb,f,g,"Initial-");
        pq.add(pn);
        while(!pq.isEmpty()){
            pn = pq.poll();
            if(isGoal(pn.getTable().getString())){
                end_time = System.nanoTime();
                solutionFoundInformed(pn,pq.size());
            }
            hn = new HashNode(pn.getMoves(),pn.getSteps());
            closed.put(pn.getTable().getString(),hn);
            for(Table son : pn.getTable().getDescedents()){
                if(!(closed.containsKey(son.getString()))){
                    g=pn.getSteps()+1;
                    h=distManhattan(son);
                    f=h+g;
                    s=putPath(pn.getMoves(),son.getLastMove());
                    tmp = new PriorityNodes(son,f,g,s);
                    pq.add(tmp);
                }
            }
        }return;
    }
    public static void tableGreedyHamming(Table tb){
        //The greedy works almost as the A*, except, f(n)=h(n)
        //Hamming heuristic
        PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
        HashMap<String,HashNode> closed = new HashMap<>();
        PriorityNodes pn,tmp;
        HashNode hn;
        String s;
        int h,g;
        g=0;
        h=distHamming(tb);
        pn=new PriorityNodes(tb,h,g,"Initial-");
        pq.add(pn);
        while(!pq.isEmpty()){
            pn=pq.poll();
            if(isGoal(pn.getTable().getString())){
                end_time = System.nanoTime();
                solutionFoundInformed(pn,pq.size());
            }
            hn=new HashNode(pn.getMoves(),pn.getSteps());
            closed.put(pn.getTable().getString(),hn);
            for(Table son : pn.getTable().getDescedents()){
                if(!closed.containsKey(son.getString())){
                    g=pn.getSteps()+1;
                    h=distHamming(son);
                    s=putPath(pn.getMoves(),son.getLastMove());
                    tmp=new PriorityNodes(son,h,g,s);
                    pq.add(tmp);
                }
            }
        }
    }
    public static void tableGreedyManhattan(Table tb){
        PriorityQueue<PriorityNodes> pq = new PriorityQueue<>();
        HashMap<String,HashNode> closed = new HashMap<>();
        PriorityNodes pn,tmp;
        HashNode hn;
        String s;
        int g,h;

        g=0;
        h=distManhattan(tb);
        pn=new PriorityNodes(tb,h,g,"Initial-");
        pq.add(pn);
        while(!pq.isEmpty()){
            pn=pq.poll();
            if(isGoal(pn.getTable().getString())){
                end_time = System.nanoTime();
                solutionFoundInformed(pn,pq.size());
            }
            hn=new HashNode(pn.getMoves(),pn.getSteps());
            closed.put(pn.getTable().getString(),hn);
            for(Table son : pn.getTable().getDescedents()){
                if(!closed.containsKey(son.getString())){
                    g=pn.getSteps()+1;
                    h=distManhattan(son);
                    s=putPath(pn.getMoves(),son.getLastMove());
                    tmp=new PriorityNodes(son,h,g,s);
                    pq.add(tmp);
                }
            }
        }
    }


/*------------------------------------------------------------------------------
Soluction Function (will terminate all the programm)
------------------------------------------------------------------------------*/
    public static void solutionFoundInformed(PriorityNodes pn, int size){
        //solution function for all Informed methods
        long total_time= end_time - start_time;
        double seconds = (double)total_time/ 1_000_000_000.0;
        System.out.println("Solução encontrada:");
        System.out.println("Número de movimentos: "+pn.getSteps());
        System.out.println("Movimentos:\n "+pn.getMoves());
        System.out.println("Número de nós criados: "+size);
        System.out.println("Tempo total: "+seconds+" segundos");
        System.exit(0);
    }
    public static void solutionFoundUnInformed(HashNode hn,int size){
        long total_time= end_time - start_time;
        double seconds = (double)total_time/ 1_000_000_000.0;
        System.out.println("Solução encontrada:");
        System.out.println("Número de movimentos: "+hn.getSteps());
        System.out.println("Movimentos:\n "+hn.getMoves());
        System.out.println("Número de nós criados: "+size);
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
                System.out.println("Solução não encontrada:");
                sec = (double)(System.nanoTime()-start_time)/ 1_000_000_000.0;
                System.out.println("Tempo: "+sec+" segundos");
            break;
            case 2://LDFS
                System.out.println("LDFS:");
                //System.out.println("O DFS sem limite de ciclo não termina :(");
                start_time=System.nanoTime();
                size=0;
                tableDFS(init,30);
                size=0;
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
