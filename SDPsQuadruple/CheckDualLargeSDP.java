import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.*;
import java.math.*;
import java.util.concurrent.*;



public class CheckDualLargeSDP{
    static Map<String, Integer> komtvoor = new TreeMap<String, Integer>();


//Dit programma verifieert "CW34DEF_22_8_10afstoddpos.dat-s"; Geeft een grens <616. Kan niet!



    public static void main(String[] args) {

	programma();
    }


     static void programma() {

	Scanner reader = new Scanner(System.in);  // Reading from System.in
	System.out.println("File name of .dat-s-file (including .dat-s)");
	String bestandsnaamdats= reader.nextLine();
	System.out.println("File name of .result-file (including .result)");
	String bestandsnaamresult= reader.nextLine();
 
        reader.close();

	 String line = "";

	int toegestaan=0;
	int countregel=1;
	int nVars=0;
	int nBlocks=0;
	List<BigDecimal[][]> DualMatrix= new ArrayList<BigDecimal[][]>();
	List<BigDecimal[][]> CFi= new ArrayList<BigDecimal[][]>();
	BigDecimal[] b = new BigDecimal[nVars+1]; //temporary initialize, because nVars still needs to be determined.
	BigDecimal[] errors = new BigDecimal[nVars+1]; //temporary initialize, because nVars still needs to be determined.


 try{    
	BufferedReader dats = new BufferedReader(new FileReader(bestandsnaamdats));
 	

        while((line =dats.readLine())!= null) {
		if(countregel==1){
			nVars=Integer.parseInt(line);    //determine nVars and initialize the vector b
			b = new BigDecimal[nVars+1];  
 			errors = new BigDecimal[nVars+1];
			b[0]=new BigDecimal("0");
			for(int var=0; var<= nVars; var++){
				List<BigDecimal[][]> F= new ArrayList<BigDecimal[][]>();
				//CFi.add(F);	
			}
		}
		if(countregel==2){
			nBlocks=Integer.parseInt(line);
		}
		if(countregel==3){
			String[] blocksizes = line.split(" ");
			for(int j=0; j< nBlocks; j++){
				int grootte = Integer.parseInt(blocksizes[j]);
				DualMatrix.add(new BigDecimal[grootte][grootte]); //we initialiseren meteen de grootte van het j-e blok in de Dualmatrix 
			}
		}
		if(countregel==4){
			String[] bstr = line.split(" ");
			for(int j=0; j< nVars; j++){
				BigDecimal b_i = new BigDecimal(bstr[j]);
				b[j+1]=b_i;
				
			}
		}		
		//We do not read the input-constraint matrices yet, to save memory. Instead, we compute the error below while reading the constraint matrices. 


	countregel++;
	}  

	System.out.println("nVars: " + nVars +", nBlocks: "+nBlocks);



	//Now we read the dual solution Y into java
	BufferedReader result = new BufferedReader(new FileReader(bestandsnaamresult));
	int dual=0;
        while(dual==0&&(line =result.readLine())!= null ) {
		if(dual==0){
			String[] dualind = line.split(" ");
			if(dualind[0].equals("yMat")){dual=1;}
		}
	}
	result.readLine();//empty line
	
	for(int j=0; j<nBlocks; j++){
		boolean moetbij =false;
		boolean moetextrabij =false;
		boolean moetgemiddeldbij =false;
		line =result.readLine();
		if(line.contains("{ {")){
			int blokgrootte=DualMatrix.get(j).length;
			String regel = line.split("[\\{\\}]")[2];	
			
			String[] getallenrij =regel.split(",");
			//vul de eerste rij van het blok

			for(int k=0; k< blokgrootte; k++){ 
					String getal="";
					if(getallenrij[k].contains(" ")){   
					 	String[] e = getallenrij[k].split(" ");	
				         	getal=e[0];
					}
					else{getal=getallenrij[k];}

					DualMatrix.get(j)[0][k]=new BigDecimal(getal) ;
					}


			int counter=1;
			//nu alle rijen tussen de eerste rij en de laatste rij
			while((line =result.readLine())!=null && !line.contains("}   }")){

			regel = line.split("[\\{\\}]")[1];	
			
			getallenrij =regel.split(",");

			for(int k=0; k< blokgrootte; k++){ 
					String getal=""; 
					if(getallenrij[k].contains(" ")){   
					 	String[] e = getallenrij[k].split(" ");	
				         	getal=e[0];
					}
					else{getal=getallenrij[k];}

					DualMatrix.get(j)[counter][k]=new BigDecimal(getal) ;

									}
			counter++;

			}
			//nu vullen we de laatste rij
			regel = line.split("[\\{\\}]")[1];	
			
			getallenrij =regel.split(",");

			for(int k=0; k< blokgrootte; k++){ 
					String getal="";
					if(getallenrij[k].contains(" ")){   
					 	String[] e = getallenrij[k].split(" ");	
				         	getal=e[0];
					}
					else{getal=getallenrij[k];}


					DualMatrix.get(j)[blokgrootte-1][k]=new BigDecimal(getal) ;
					}

		}
		else{
			
			String regel = line.split("[\\{\\}]")[1];
			DualMatrix.get(j)[0][0]=new BigDecimal(regel);
		}
	}


	//Now the dual matrix Y is read into java.

	 countregel=1;
	dats = new BufferedReader(new FileReader(bestandsnaamdats));
	BigDecimal[] Inprods = new BigDecimal[nVars+1];
	String[] cline;
	int matnum;
	int bloknum; 
	int rijnum;
	int kolomnum; 
			for(int j=0; j< nVars+1; j++){
				Inprods[j]=new BigDecimal("0");
				
			}

        while((line =dats.readLine())!= null) {
		if(countregel==1){}
		if(countregel==2){}
		if(countregel==3){}
		if(countregel==4){}
			
             // We compute the error while reading the constraint matrices from the input. 
	     // Errors[i]=<F_i,Y>-b_i
	     // Inprods[i]=<F_i,Y>

		if(countregel>=5){
			cline = line.split(" ");
			//een constraint-regel bestaat uit 5 entries: 
			//welke matrix (i van F_i, 0 betekent C), welk blok, welke rij, welke kolom, waarde.
			matnum = Integer.parseInt(cline[0]);	
			bloknum = Integer.parseInt(cline[1]);
			rijnum = Integer.parseInt(cline[2]);
			kolomnum = Integer.parseInt(cline[3]);
			Inprods[matnum]=Inprods[matnum].add(
				DualMatrix.get(bloknum-1)[rijnum-1][kolomnum-1].multiply(new BigDecimal(cline[4])) );
			if(rijnum!=kolomnum){Inprods[matnum]=Inprods[matnum].add(
				DualMatrix.get(bloknum-1)[kolomnum-1][rijnum-1].multiply(new BigDecimal(cline[4])) );}  
					

		}	
		countregel++;
	}
        dats.close();
	System.out.println("ERROR-reading finished");
	BigDecimal Totaalfout = new BigDecimal("0");
        //"Totaalfout" will be the total error sum abs(eps_i)
	BigDecimal[] Errors = new BigDecimal[nVars+1];
			for(int j=1; j< nVars+1; j++){
				Errors[j]=Inprods[j].subtract(b[j]);
				Totaalfout=Totaalfout.add(Errors[j].abs());
				
			}
			System.out.println("DUAL OBJECTIVE VALUE: "+ Inprods[0]);
			System.out.println("total error sum of the absolute values of the eps_i: "+ Totaalfout);
 


		System.out.println("DUAL MATRIX Y, NOT-SYMMETRIC or NOT-POS-DEF: ");
		int aantalnietsdd=0;
		for(int j=0; j< nBlocks; j++){

			int tempgrootte=DualMatrix.get(j).length;

			if(!isSym(DualMatrix.get(j)) || !isPrincPos(DualMatrix.get(j)) ){
			aantalnietsdd++;
	
				System.out.println("NOT SYMPOSDEF, Matrix Block number= "+j);
		

		
			System.out.println("");	}
		} 

	System.out.println("NUMBER OF NON-SYMMETRIC OR NON-POSDEF BLOCKS: "+ aantalnietsdd);

	
}catch (IOException e) { } 

}


    static boolean isSym(BigDecimal[][] matrix){
	int tempgrootte = matrix.length;
	boolean isSym=true;

        for(int r=0; r< tempgrootte; r++){
	for(int c=0; c< tempgrootte; c++){
		if(!matrix[r][c].equals(matrix[c][r])){isSym=false; System.out.println("NOT SYMMETRIC");}
				
	}}

	if(isSym){return true;}else{return false;}

    }



    
    // kth leading principal minors test: delete the last k rows and columns and check that the determinant 
    // of the resulting matrix is >0, for all k=0,...,n-1. Necessary and sufficient for positive definiteness. 
 
   // NOT necessary for positive SEMIdefiniteness (but sufficient)

    static boolean isPrincPos(BigDecimal[][] matrix){
    	int N=matrix.length;	
	boolean isPosDef = true;
	for(int k=0; k<N; k++){
	    BigDecimal[][] subM=new BigDecimal[N-k][N-k];
	    int tempgrootte=N-k;
		
	    
	    for(int r=0; r< tempgrootte; r++){
	    for(int c=0; c< tempgrootte; c++){
		subM[r][c]=matrix[r][c];				
	    }}
		
	   	//System.out.println("det ="+ determinant(subM));
            if(detCrout(subM).compareTo(new BigDecimal("0"))!=1){
    		isPosDef=false;
	    }		 

	
	}
	return isPosDef;
    }

  public static BigDecimal detCrout(BigDecimal a[][]) {
    	int n=a.length;	
    try {
      for (int i = 0; i < n; i++) {
        boolean nonzero = false;
        for (int j = 0; j < n; j++)
          if (a[i][j].compareTo(new BigDecimal(BigInteger.ZERO)) > 0)
            nonzero = true;
        if (!nonzero)
          return BigDecimal.ZERO;
      }

      BigDecimal scaling[] = new BigDecimal[n];
      for (int i = 0; i < n; i++) {
        BigDecimal big = new BigDecimal(BigInteger.ZERO);
        for (int j = 0; j < n; j++)
          if (a[i][j].abs().compareTo(big) > 0)
            big = a[i][j].abs();
        scaling[i] = (new BigDecimal(BigInteger.ONE)).divide(big, 75, BigDecimal.ROUND_HALF_EVEN);
      }

      int sign = 1;

      for (int j = 0; j < n; j++) {

        for (int i = 0; i < j; i++) {
          BigDecimal sum = a[i][j];
          for (int k = 0; k < i; k++)
            sum = sum.subtract(a[i][k].multiply(a[k][j]));
          a[i][j] = sum;
        }

        BigDecimal big = new BigDecimal(BigInteger.ZERO);
        int imax = -1;
        for (int i = j; i < n; i++) {
          BigDecimal sum = a[i][j];
          for (int k = 0; k < j; k++)
            sum = sum.subtract(a[i][k].multiply(a[k][j]));
          a[i][j] = sum;
          BigDecimal cur = sum.abs();
          cur = cur.multiply(scaling[i]);
          if (cur.compareTo(big) >= 0) {
            big = cur;
            imax = i;
          }
        }

        if (j != imax) {

          for (int k = 0; k < n; k++) {
            BigDecimal t = a[j][k];
            a[j][k] = a[imax][k];
            a[imax][k] = t;
          }

          BigDecimal t = scaling[imax];
          scaling[imax] = scaling[j];
          scaling[j] = t;

          sign = -sign;
        }

        if (j != n - 1)
          for (int i = j + 1; i < n; i++)
            a[i][j] = a[i][j].divide(a[j][j], 75, BigDecimal.ROUND_HALF_EVEN);

      }

      BigDecimal result = new BigDecimal(1);
      if (sign == -1)
        result = result.negate();
      for (int i = 0; i < n; i++)
        result = result.multiply(a[i][i]);

      return result;

    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }

}

