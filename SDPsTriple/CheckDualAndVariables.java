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
import java.util.Scanner;




public class CheckDualAndVariables{
    static Map<String, Integer> komtvoor = new TreeMap<String, Integer>();



    public static void main(String[] args) {

	programma();
    }


     static void programma() {
	Scanner reader = new Scanner(System.in);  // Reading from System.in
	System.out.println("File name of .dat-s-file (including .dat-s)");
	String bestandsnaamdats= reader.nextLine();
	System.out.println("File name of .result-file (including .result)");
	String bestandsnaamresult= reader.nextLine();
	System.out.println("File name of variables file (including .txt)");
	String bestandsnaammonomials= reader.nextLine();


reader.close();


        String line = "";

	int toegestaan=0;
	int countregel=1;
	int nVars=0;
	int nBlocks=0;
	List<BigDecimal[][]> DualMatrix= new ArrayList<BigDecimal[][]>();
	List<List<BigDecimal[][]>> CFi= new ArrayList<List<BigDecimal[][]>>();
	BigDecimal[] b = new BigDecimal[nVars+1]; //temporary initialize, because nVars still needs to be determined.



 try{    
	BufferedReader dats = new BufferedReader(new FileReader(bestandsnaamdats));
 	//First we are going to read the dat-s file.

        while((line =dats.readLine())!= null) {
		if(countregel==1){
			nVars=Integer.parseInt(line);    //determine nVars and initialize the vector b
			b = new BigDecimal[nVars+1];  
			b[0]=new BigDecimal("0");
			for(int var=0; var<= nVars; var++){
				List<BigDecimal[][]> F= new ArrayList<BigDecimal[][]>();
				CFi.add(F);	
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
				
				for(int var=0; var<= nVars; var++){
					CFi.get(var).add(new BigDecimal[grootte][grootte]);  //Hier initialiseren we de grootte van alle F_i en van C (=F_0)
				}
			}
		}
		if(countregel==4){
			String[] bstr = line.split(" ");
			for(int j=0; j< nVars; j++){
				BigDecimal b_i = new BigDecimal(bstr[j]);
				b[j+1]=b_i;
				
			}
		}
		
		//Nu we fill the constraint matrices
		if(countregel>=5){
			String[] cline = line.split(" ");
			//een constraint-regel bestaat uit 5 entries: 
			//welke matrix (i van F_i, 0 betekent C), welk blok, welke rij, welke kolom, waarde.
			int matnum = Integer.parseInt(cline[0]);
			int bloknum = Integer.parseInt(cline[1]);
			int rijnum = Integer.parseInt(cline[2]);
			int kolomnum = Integer.parseInt(cline[3]);
			BigDecimal waarde = new BigDecimal(cline[4]);

			CFi.get(matnum).get(bloknum-1)[rijnum-1][kolomnum-1]=waarde;

		}
		
		countregel++;
	}
        dats.close();


//This code can print the constraint matrices. (Now commented, because we don't need to print them). 
/*
	for(int j=0; j<= nVars; j++){
		if(j==0){//we printen even alleen constraint matrix 0.
		System.out.println("Constraint matrix F"+j + ": ");
		for(int k=0; k< nBlocks; k++){
			int tempgrootte=CFi.get(j).get(k).length;
			
			for(int r=0; r< tempgrootte; r++){ System.out.print("[ ");
			for(int c=0; c< tempgrootte; c++){
				if(c>=r){System.out.print(""+CFi.get(j).get(k)[r][c]+" ");}
				else{System.out.print(""+CFi.get(j).get(k)[c][r]+" ");} 			
			}
			System.out.print("] \n");
			}
		
			System.out.println("");	
		}

	}	
	}*/   

	System.out.println("nVars: " + nVars +", nBlocks: "+nBlocks);

	//Now the input is read into java.

	//Now we are reading the dual matrix Y. (not so efficient, but correct.)
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
		line =result.readLine();
		if(line.contains("{ {")){
			int blokgrootte=DualMatrix.get(j).length;
			String regel = line.split("[\\{\\}]")[2];	
			
			String[] getallenrij =regel.split(",");
			//fill the first row of the block

			for(int k=0; k< blokgrootte; k++){ 
					String getal="";
					if(getallenrij[k].contains(" ")){   
					 	String[] e = getallenrij[k].split(" ");	
				         	getal=e[0];
					}
					else{getal=getallenrij[k];}

					DualMatrix.get(j)[0][k]=new BigDecimal(getal) ;}


			int counter=1;
			//now all rows between the first & the last row
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

					DualMatrix.get(j)[counter][k]=new BigDecimal(getal) ;}
			counter++;

			}
			//now we fill the last row
			regel = line.split("[\\{\\}]")[1];	
			
			getallenrij =regel.split(",");

			for(int k=0; k< blokgrootte; k++){ 
					String getal="";
					if(getallenrij[k].contains(" ")){   
					 	String[] e = getallenrij[k].split(" ");	
				         	getal=e[0];
					}
					else{getal=getallenrij[k];}


					DualMatrix.get(j)[blokgrootte-1][k]=new BigDecimal(getal) ;}


		}
		else{
			
			String regel = line.split("[\\{\\}]")[1];
			DualMatrix.get(j)[0][0]=new BigDecimal(regel);
		}
	}


	//NOW THE DUAL MATRIX IS READ INTO JAVA



	//WE CHECK EACH BLOCK IN THE DUAL MATRIX: IS IT POSITIVE DEFINITE?
		
		System.out.println("DUAL MATRIX Y, NOT-SYMMETRIC or NOT-POS-DEF: ");
		int aantalnietsdd=0;
		for(int k=0; k< nBlocks; k++){
		
			if(!isSym(DualMatrix.get(k)) || !isPrincPos(DualMatrix.get(k))){
			aantalnietsdd++;
			int tempgrootte=DualMatrix.get(k).length;
			
			for(int r=0; r< tempgrootte; r++){ System.out.print("[ ");
			for(int c=0; c< tempgrootte; c++){
				System.out.print(""+DualMatrix.get(k)[r][c]+" ");			
			}
			System.out.print("] \n");
			}
		
			System.out.println("");	}
		} 

	System.out.println("NUMBER OF NON-SYMMETRIC OR NON-POSDEF BLOCKS: "+ aantalnietsdd);


	BigDecimal[] Errors = new BigDecimal[nVars+1]; //Errors[0] will be the obj value of the dual program, Errors[i]=<F_i,Y>-b_i.

	for(int j=0; j<=nVars; j++){
		BigDecimal inProd = new BigDecimal("0");
		
		for(int k=0; k< nBlocks; k++){
			int tempgrootte=DualMatrix.get(k).length;
			for(int r=0; r< tempgrootte; r++){
			for(int c=0; c< tempgrootte; c++){


				if(c>=r){ if(CFi.get(j).get(k)[r][c]!=null){inProd=inProd.add(DualMatrix.get(k)[r][c].multiply(CFi.get(j).get(k)[r][c]));}}
				else{if(CFi.get(j).get(k)[c][r]!=null){inProd=inProd.add(DualMatrix.get(k)[r][c].multiply(CFi.get(j).get(k)[c][r]));}} 
				
			}}


		}
	
		Errors[j]=inProd.subtract(b[j]);


	}
//"totaalfout" will be the total error sum abs(eps_i)

	BigDecimal totaalFout=new BigDecimal("0");

	for(int j=1; j<=nVars; j++){
	totaalFout=totaalFout.add(Errors[j].abs());
	}

	System.out.println("dualObj "+ Errors[0]);
	System.out.println("total error sum of the absolute values of the eps_i: "+ totaalFout);
	
        System.out.println("");


        BufferedReader monomen = new BufferedReader(new FileReader(bestandsnaammonomials));
        String currentvariable = "";
	int allowed=0;
	int nummer=0;

        System.out.println("Allowed variables:");
	for(int k=(nBlocks-nVars); k< nBlocks; k++){
            currentvariable =monomen.readLine();

	    if((DualMatrix.get(k)[0][0]).compareTo(new BigDecimal("1E-50"))==-1){allowed++;
				//System.out.println(nummer);
	          System.out.println(currentvariable);
            } 

        }
	System.out.println("NUMBER OF ALLOWED VARIABLES =" + allowed);

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





    static BigDecimal determinant (BigDecimal A[][])  {
	//Deze methode komt van Sanfoundry.com  en is een kant-en-klare methode om determinanten van willekeurige matrices te berekenen. De code is gecontroleerd en aangepast om niet double[][] matrices, maar BigDecimal[][] matrices te bekijken.
        BigDecimal det=new BigDecimal("0");
	int N=A.length;	
        if(N == 1){det = A[0][0];}
        else if (N == 2){det = (A[0][0].multiply(A[1][1])).subtract(A[1][0].multiply(A[0][1]));}
        else { det=new BigDecimal("0");

            for(int j1=0;j1<N;j1++){

                BigDecimal[][] m = new BigDecimal[N-1][];

                for(int k=0;k<(N-1);k++){
                    m[k] = new BigDecimal[N-1];
                }
                for(int i=1;i<N;i++)
                {
                    int j2=0;
                    for(int j=0;j<N;j++)
                    {
                        if(j == j1)
                            continue;
                        m[i-1][j2] = A[i][j];
                        j2++;
                    }
                }
		if(j1%2==0){
			det=det.add(A[0][j1].multiply(determinant(m)));
		}else{
			det=det.subtract(A[0][j1].multiply(determinant(m)));
		}
            }
        }
        return det;
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
            if(determinant(subM).compareTo(new BigDecimal("0"))!=1){
    		isPosDef=false;
	    }		 

	
	}
	return isPosDef;
    }

}

