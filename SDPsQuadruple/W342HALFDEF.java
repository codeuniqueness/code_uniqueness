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
import org.mapdb.*; //BELANGRIJK OM GROTERE GEVALLEN AAN TE KUNNEN


public class W342HALFDEF{


    //Een aantal variabelen die in het hele programma gebruikt worden, worden hier geinitialiseerd.
    static ConcurrentNavigableMap<String, Integer>  nummers = DBMaker.tempTreeMap();
    //static Map<String, Integer> nummers = new TreeMap<String, Integer>();  
    static int nummer;
    static Map<String, String> formules = new TreeMap<String, String>();

    //We schrijven een aantal determinanten op die we veel zullen gebruiken bij het maken van de polynomen
    static final String det2 = " 1 x11x22 -1 x21x12";
    static final String det3 = " 1 x11x22x33 -1 x11x23x32 -1 x12x21x33 +1 x12x23x31 +1 x13x21x32 -1 x13x22x31";
    static final String det4 = " 1 x11x22x33x44 -1 x11x22x34x43 -1 x11x23x32x44 +1 x11x23x34x42 +1 x11x24x32x43 -1 x11x24x33x42 -1 x12x21x33x44 +1 x12x21x34x43 +1 x12x23x31x44 -1 x12x23x34x41 -1 x12x24x31x43 +1 x12x24x33x41 +1 x13x21x32x44 -1 x13x21x34x42 -1 x13x22x31x44 +1 x13x22x34x41 +1 x13x24x31x42 -1 x13x24x32x41 -1 x14x21x32x43 +1 x14x21x33x42 +1 x14x22x31x43 -1 x14x22x33x41 -1 x14x23x31x42 +1 x14x23x32x41";


    static Map<String, String> AB = new TreeMap<String, String>();
    static Map<String, String> ABCD = new TreeMap<String, String>();
    static Map<String, String> ACC = new TreeMap<String, String>();
    static Map<String, String> MON01 = new TreeMap<String, String>();

    static Map<String, Integer> parts = new TreeMap<String, Integer>();

    static Map<Integer, Integer> leegcoef = new TreeMap<Integer, Integer>();

    static Map<String, String> partmon = new TreeMap<String, String>();
    static ConcurrentNavigableMap<String, String[]>  monomen = DBMaker.tempTreeMap();



    public static void main(String[] args) {


        System.out.println("\nWelcome to the constant weight binary code bounds java program for computing B_4(n,d,w). THIS VERSION ONLY WORKS FOR w=n/2");
        System.out.println("Created by Sven Polak");

	int n=0; int d=0; int w=0;
	if (args.length > 0) {
    	try {
        	n = Integer.parseInt(args[0]);
   	 } catch (NumberFormatException e) {
        	System.err.println("Argument" + args[0] + " must be an integer.");
      	  System.exit(1);
    	}    	try {
        	d = Integer.parseInt(args[1]);
   	 } catch (NumberFormatException e) {
        	System.err.println("Argument" + args[1] + " must be an integer.");
      	  System.exit(1);
    	}    	try {
        	w = Integer.parseInt(args[2]);
   	 } catch (NumberFormatException e) {
        	System.err.println("Argument" + args[2] + " must be an integer.");
      	  System.exit(1);
    	}
	}



        System.out.println("");
        //System.out.println("Give n, the size of the words:");	
	//Scanner in = new Scanner(System.in);
	//int n = in.nextInt();
        System.out.println("n= " + n);
        //System.out.println("Give d, the distance:");	
	//int d = in.nextInt();
        System.out.println("d= " + d);
	//System.out.println("Give w, the weight:");
	//int w = in.nextInt();
        System.out.println("w= " + w);

	final long startTime = System.currentTimeMillis();
        programma(n,d,w); 
	final long endTime = System.currentTimeMillis();
	System.out.println("Total execution time: " + (endTime - startTime)/1000.0 + " seconds" );
    }


     static void programma(int n, int d, int w) {


/*
#---------------------------------------------------------------------------------------
# We start by numbering the variables in the SDP. An Sn-orbit of a code of size <=4 can be represented by a monomial in x_ijkl, with i,j,k,l \in {0,1}.
#
# x11 = x0000   i1
# x12 = x0001   i2 
# x13 = x0010   i3 
# x14 = x0011   i4

# x21 = x0100   i5
# x22 = x0101   i6
# x23 = x0110   i7
# x24 = x0111   i8

# x31 = x1000   i9 
# x32 = x1001   i10
# x33 = x1010   i11
# x34 = x1011   i12

# x41 = x1100   i13
# x42 = x1101   i14 
# x43 = x1110   i15
# x44 = x1111   i16
##
*/
  


   // 1,2,3,4 zijn de formules voor viertallen, |S|=2 constant weight

    formules.put("111", " 1 x31");	
    formules.put("112", " 1 x32");	
    formules.put("121", " 1 x33");	
    formules.put("122", " 1 x34");	

    formules.put("211", " 1 x41");	
    formules.put("212", " 1 x42");	
    formules.put("221", " 1 x43");	
    formules.put("222", " 1 x44");


    formules.put("311", " 1 x21");	
    formules.put("312", " 1 x22");	
    formules.put("321", " 1 x23");	
    formules.put("322", " 1 x24");	

    formules.put("411", " 1 x11");	
    formules.put("412", " 1 x12");	
    formules.put("421", " 1 x13");	
    formules.put("422", " 1 x14");

   // 5,6 zijn de formules voor drietallen, S={een woord van constant weight w}.


    formules.put("511", " 1 x31");	
    formules.put("512", " 1 x34");	
    formules.put("521", " 1 x41");	
    formules.put("522", " 1 x44");	

    formules.put("611", " 1 x11");	
    formules.put("612", " 1 x14");	
    formules.put("621", " 1 x21");	
    formules.put("622", " 1 x24");


   // 7 zijn de formules voor tweetallen, S=leeg.


    formules.put("711", " 1 x11");	
    formules.put("712", " 1 x14");	
    formules.put("721", " 1 x41");	
    formules.put("722", " 1 x44");	



    partmon.put("11", "x0000");	
    partmon.put("12", "x0001");	
    partmon.put("13", "x0010");	
    partmon.put("14", "x0011");	
    partmon.put("21", "x0100");	
    partmon.put("22", "x0101");	
    partmon.put("23", "x0110");	
    partmon.put("24", "x0111");	
    partmon.put("31", "x1000");	
    partmon.put("32", "x1001");	
    partmon.put("33", "x1010");	
    partmon.put("34", "x1011");	
    partmon.put("41", "x1100");	
    partmon.put("42", "x1101");	
    partmon.put("43", "x1110");	
    partmon.put("44", "x1111");	






  //  Map<String, Integer> parts = new TreeMap<String, Integer>();
    parts.put("11", Integer.valueOf(0));
    parts.put("12", Integer.valueOf(1));
    parts.put("13", Integer.valueOf(1));
    parts.put("14", Integer.valueOf(1));
    parts.put("21", Integer.valueOf(1));
    parts.put("22", Integer.valueOf(1));
    parts.put("23", Integer.valueOf(1));
    parts.put("24", Integer.valueOf(1));
    parts.put("31", Integer.valueOf(1));
    parts.put("32", Integer.valueOf(1));
    parts.put("33", Integer.valueOf(1));
    parts.put("34", Integer.valueOf(1));
    parts.put("41", Integer.valueOf(1));
    parts.put("42", Integer.valueOf(1));
    parts.put("43", Integer.valueOf(1));
    parts.put("44", Integer.valueOf(1));


//Initialiseer AB, ABCD en ACC
	
    for(Map.Entry<String, Integer> partitie : parts.entrySet()) {
    String part = partitie.getKey();
    AB.put(part,part);
    }


    AB.put("21","31");  AB.put("22","32");  AB.put("23","33");  AB.put("24","34"); AB.put("31","21"); AB.put("32","22"); AB.put("33","23"); AB.put("34","24");

//S4 wordt gegenereerd door de twee-cykel (12) en de vier-cykel (1234). 
    ABCD.put("11","11");    ABCD.put("12","31");   ABCD.put("13","12");   ABCD.put("14","32");   ABCD.put("21","13");   ABCD.put("22","33");   ABCD.put("23","14");
    ABCD.put("24","34");    ABCD.put("31","21");   ABCD.put("32","41");   ABCD.put("33","22");   ABCD.put("34","42");   ABCD.put("41","23");   ABCD.put("42","43");
    ABCD.put("43","24");    ABCD.put("44","44");

//
    ACC.put("11","11");   ACC.put("12","12");    ACC.put("13","23");   ACC.put("14","24");   ACC.put("41","31");   ACC.put("42","32");  ACC.put("43","43");  ACC.put("44","44");

    MON01.put("11","44");    MON01.put("44","11");    MON01.put("12","43");    MON01.put("43","12");    MON01.put("13","42");    MON01.put("42","13");
    MON01.put("14","41");    MON01.put("41","14");     MON01.put("21","34");    MON01.put("34","21");    MON01.put("22","33");    MON01.put("33","22");    MON01.put("23","32");    MON01.put("32","23");    MON01.put("24","31");    MON01.put("31","24");

    String var;
    String monnw;

//Stel de monoomgrootte vast.
final int monoomgrootte = 3*n;


String part;

StringBuilder monoombuilder;
StringBuilder monabbuilder;
StringBuilder monABCDbuilder;
StringBuilder monaccbuilder;
StringBuilder mon01builder;


int afstandab;
int afstandac;
int afstandad;
int afstandbc;
int afstandbd;
int afstandcd;

int w1; 
int w2;
int w3;
int w4;

	final long startloop = System.currentTimeMillis();

for (int i1=n; i1>= 0; i1--){ 
System.out.println("ronde "+ (n-i1));
for (int i2=n-i1; i2>= 0; i2--){
if(i2<=w){
for (int i3=n-i1-i2; i3>= 0; i3--){ 
if(i3<=w){
for (int i4=n-i1-i2-i3; i4>= 0; i4--){ 
if(i2+i4<=w && i3+i4<=w){ 
for (int i5=n-i1-i2-i3-i4; i5>= 0; i5--){   
if(i5<=w){
for (int i6=n-i1-i2-i3-i4-i5; i6>= 0; i6--){ 
if(i6+i5<=w && i6+i4+i2<=w){ 
for (int i7=n-i1-i2-i3-i4-i5-i6; i7>= 0; i7--){  
if(i7+i6+i5<=w&& i7+i3+i4 <=w){
for (int i8=n-i1-i2-i3-i4-i5-i6-i7; i8>= 0; i8--){ 
if(i8+i7+i6+i5<=w && i8+i7+i3+i4 <=w && i8+i6+i4+i2<=w){
for (int i9=n-i1-i2-i3-i4-i5-i6-i7-i8; i9>= 0; i9--){    
if(i9<=w){
for (int i10=n-i1-i2-i3-i4-i5-i6-i7-i8-i9; i10>= 0; i10--){ 
if(i10+i9<=w && i10+ i8+i6+i4+i2<=w){        
for (int i11=n-i1-i2-i3-i4-i5-i6-i7-i8-i9-i10; i11>= 0; i11--){  
if(i11+i10+i9<=w && i11+i8+i7+i3+i4<=w){ 
for (int i12=n-i1-i2-i3-i4-i5-i6-i7-i8-i9-i10-i11; i12>= 0; i12--){     
if(i12+i11+i10+i9<=w && i12+i11+i8+i7+i3+i4<=w && i12+i10+i8+i6+i4+i2<=w ) {
for (int i13=n-i1-i2-i3-i4-i5-i6-i7-i8-i9-i10-i11-i12; i13>= 0; i13--){  
if(i13+i12+i11+i10+i9<=w && i13+i8+i7+i6+i5<=w){
for (int i14=n-i1-i2-i3-i4-i5-i6-i7-i8-i9-i10-i11-i12-i13; i14>= 0; i14--){
if(i14+i13+i12+i11+i10+i9<=w && i14+i13+i8+i7+i6+i5<=w && i14+i12+i10+i8+i6+i4+i2<=w){
for (int i15=n-i1-i2-i3-i4-i5-i6-i7-i8-i9-i10-i11-i12-i13-i14; i15>= 0; i15--){ 
if(i15<=w){   
int i16=n-i1-i2-i3-i4-i5-i6-i7-i8-i9-i10-i11-i12-i13-i14-i15;   


//aantal 1en eerste woord
w1=i9+i10+i11+i12+i13+i14+i15+i16;
//aantal 1en tweede woord
w2=i5+i6+i7+i8+i13+i14+i15+i16;
//aantal 1en derde woord
w3=i3+i4+i7+i8+i11+i12+i15+i16;
//aantal 1en vierde woord
w4=i2+i4+i6+i8+i10+i12+i14+i16;

 
if(w1==w && w2==w && w3==w && w4==w ){





//aantal posities niet aabc
afstandab=n-(i1+i2+i3+i4+i13+i14+i15+i16);
//aantal posities niet abac
afstandac=n-(i1+i2+i5+i6+i11+i12+i15+i16);
//aantal posities niet abca
afstandad=n-(i1+i3+i5+i7+i10+i12+i14+i16);
//aantal posities niet abbc
afstandbc=n-(i1+i2+i7+i8+i9+i10+i15+i16);
//aantal posities niet abcb
afstandbd=n-(i1+i3+i6+i8+i9+i11+i14+i16);
//aantal posities niet abcc
afstandcd=n-(i1+i4+i5+i8+i9+i12+i13+i16);



int mindist=n+1;

if (afstandab> 0 && afstandab < mindist) {mindist =afstandab;}
if (afstandac > 0 && afstandac   <  mindist) {mindist=afstandac ;}
if (afstandad > 0 && afstandad <  mindist) {mindist=afstandad;}
if (afstandbc > 0 && afstandbc  <  mindist) {mindist= afstandbc;}
if (afstandbd > 0 && afstandbd  <  mindist) {mindist= afstandbd ;}
if (afstandcd > 0 && afstandcd   <  mindist) {mindist = afstandcd ;}



//GA ALLEEN OVER MONOMEN MET MINAFST >=d!!!
if(mindist >=d){



//We gebruiken StringBuilders van vaste grootte om wat geheugen uit te sparen.
monoombuilder = new StringBuilder(monoomgrootte);
monabbuilder = new StringBuilder(monoomgrootte);
monABCDbuilder = new StringBuilder(monoomgrootte);
monaccbuilder = new StringBuilder(monoomgrootte);
mon01builder=new StringBuilder(monoomgrootte);


for (int i= 1; i<= i1; i++){
part="11";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

monaccbuilder.append("x");
monaccbuilder.append(ACC.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


for (int i= 1; i<= i2; i++){
part="12";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

monaccbuilder.append("x");
monaccbuilder.append(ACC.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


for (int i= 1; i<= i3; i++){
part="13";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

monaccbuilder.append("x");
monaccbuilder.append(ACC.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


for (int i= 1; i<= i4; i++){
part="14";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

monaccbuilder.append("x");
monaccbuilder.append(ACC.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}



for (int i= 1; i<= i5; i++){
part="21";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


for (int i= 1; i<= i6; i++){
part="22";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));


}




for (int i= 1; i<= i7; i++){
part="23";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));


mon01builder.append("x");
mon01builder.append(MON01.get(part));


}



for (int i= 1; i<= i8; i++){
part="24";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}

//Vanaf nu pas vanaf part41 weer ACC

for (int i= 1; i<= i9; i++){
part="31";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


for (int i= 1; i<= i10; i++){
part="32";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}



for (int i= 1; i<= i11; i++){
part="33";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


for (int i= 1; i<= i12; i++){
part="34";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}

for (int i= 1; i<= i13; i++){
part="41";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

monaccbuilder.append("x");
monaccbuilder.append(ACC.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


for (int i= 1; i<= i14; i++){
part="42";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));


monaccbuilder.append("x");
monaccbuilder.append(ACC.get(part));


mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


for (int i= 1; i<= i15; i++){
part="43";

monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

monaccbuilder.append("x");
monaccbuilder.append(ACC.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}



for (int i= 1; i<= i16; i++){
part="44";


monoombuilder.append("x");
monoombuilder.append(part);


monabbuilder.append("x");
monabbuilder.append(AB.get(part));

monABCDbuilder.append("x");
monABCDbuilder.append(ABCD.get(part));

monaccbuilder.append("x");
monaccbuilder.append(ACC.get(part));

mon01builder.append("x");
mon01builder.append(MON01.get(part));

}


 
String mon= monoombuilder.toString();

String monab=monabbuilder.toString();
String monABCD=monABCDbuilder.toString();
String monacc=monaccbuilder.toString();
String mon01 = mon01builder.toString();


//Sorteer de monomen.
monab=maal_mon("",monab);
monABCD=maal_mon("",monABCD);
monacc=maal_mon("",monacc);
mon01=maal_mon("",mon01);


String[] monoomlijst;

if(afstandab==0) {
monomen.put(mon,  new String[] {monab,monABCD,mon01,monacc}); 
}
else {
monomen.put(mon,  new String[] {monab,monABCD, mon01});
}


}


}}}}}}}}}}}}}}}
}}}}}}}}}}}}}}}

/*#
# WE GAAN EERST DE MONOMEN AF DIE SINGLETON OF DOUBLETON CODES REPRESENTEREN (WE HEBBEN DE NUMMERS NODIG)
#
# VOOR 4-TALLEN
*/


Writer MONwriter = null; 

try{ MONwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("monomialrepresentatives_quadruples_"+n+"_"+d+"_"+w+ ".txt"), "utf-8"));
       	nummer=0;
	String mon="";
	String var00="x11";
	String var11="x44";
	String var01="x14";
	String var10="x41";
	nummer=nummer+1;
			
	for (int i=1; i<=w; i++) {
	    mon=maal_mon(mon,var11);
	}
	for (int i=w+1;i<=n;i++) {
	    mon=maal_mon(mon,var00);
        }
	//leegcoef.put(nummer, binomial(n,w) ); //COEF= n choose w;
	nummers.put(mon,nummer);
	MONwriter.write(zetomin01_mon(mon)+"\n");

// y2 = orbit of two monomials at distance d, y3 = orbit of two monomials at distance d+2; y4 = orbit of two monomials at distance d+4; y5=orbit of two monomials at distance d+6, etc.

      	for (int t=d; t<=n; t++){
		mon="";
		if(t >=d && t%2==0  && t/2+w <=n && t <=2*w)	{
			nummer=nummer+1;

			int halft = t/2; 		
			for (int i=1; i<=halft; i++) {
				mon=maal_mon(mon,var10);
			}
			for (int i=halft+1;i<=w;i++) {
				mon=maal_mon(mon,var11);
			}
			for (int i=w+1;i<=w+halft;i++) {
				mon=maal_mon(mon,var01);
			}
			for (int i=w+halft+1;i<=n;i++) {
				mon=maal_mon(mon,var00);
			}
			System.out.println(mon);
			MONwriter.write(zetomin01_mon(mon)+"\n");
			
			verwerk(mon);
		}
	}
/*
#
# NU DE OVERIGE MONOMEN
#*/

    for (Map.Entry<String, String[]> monoomiterator: monomen.entrySet()) {
        String monoom = monoomiterator.getKey();
		if (nummers.get(monoom)==null ||nummers.get(monoom)==0) {
			nummer=nummer+1;
			MONwriter.write(zetomin01_mon(monoom)+"\n");
			leegcoef.put(nummer,0);
			verwerk(monoom);
		}
    }

	System.out.println("aantal variabelen= " +nummer);

}catch (IOException e) { } 
finally {
	if (MONwriter != null) {
                try {
                    MONwriter.close(); 
                } catch (Exception e) { }
	}
} //now the file with monomial representatives has been written.


/*--------------------------------------------------------------------------------
#
# NOW THE VARIABLES HAVE BEEN COMPUTED, WE CAN START WRITING THE SDP-FILE
#*/ 




int bloknr=0;
Writer SDPwriter = null; 

try{ SDPwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("CW34HALFDEF_"+n+"_"+d+"_"+w+ ".sdp"), "utf-8"));


SDPwriter.write("Maximize\n");
    SDPwriter.write( " +1 y1\n");

    SDPwriter.write("Subject To\n");

//
// We doen het geval C= empty (in the article C is called "D")
//
    SDPwriter.write("# C=leeg, tweetallen\n");
//

//mu1=grootte eerste tableau.
int mu1=n;
//shape van het eerste tableau van hoogte 2
for ( int t1 =0; t1<=n/2;t1++){


String startP1pol=" 1 ";
for(int s=1; s<=mu1-2*t1; s=s+1){
startP1pol=maal_pol(startP1pol," 1 x11");
}
for(int s=1; s<=t1; s=s+1){
startP1pol=maal_pol(startP1pol,det2);
}






bloknr++;
int blokgrootte = blocksizeleeg(n,t1,d,w);


String[] bovenmatrix= new String[blokgrootte+1];
String[][] polymatrix = new String[blokgrootte+1][blokgrootte+1];




System.out.println("de grootte van blok " + bloknr + ", het blok corresponderend met de partitie, ("+ n+", " +t1 +") wordt: "+  blokgrootte);





int row=1; int col=1;


//content van rijtableau,1
for ( int r1 =mu1-t1; r1>=t1;r1--){

int e11 = r1;
int e12 = n-r1;


if(e12==w ){


if(t1==0){
//Maak bovensterijpolynoom
String Pol="";
String var00="x11";
String var11="x44";
//String var01="x14";
//String var10="x41";
 for (int i=1; i<=e11; i++) {
	Pol=maal_mon(Pol,var00);
 }
 for (int i=1;i<=e12;i++) {
	Pol=maal_mon(Pol,var11);
 }
 BigInteger coefficientleeg=  new BigInteger("1"); 
 Pol=" " + coefficientleeg +" "+ Pol;

 bovenmatrix[row]=linear(Pol);

}




//content van kolomtableau,1
for ( int r2 =n-t1; r2>=t1;r2--){


int e21 = r2;
int e22 = n-r2;


if(e22==w){


if(col >= row){


//MAAKPOLYNOOM


String temppol1= zetom_pol( mk_poleentab(n, t1, r1, r2, startP1pol), "7");



String finalpol = linear(temppol1);

if(t1==0){
finalpol=linear(deeldoorconstante_pol(temppol1,binomial(n,e12)));
}


polymatrix[row][col]= finalpol;    
}else{polymatrix[row][col]= "";    }









col++;
//Einde col>=row


//Einde r2, distanceparameter2
}}


col=1; 





row++;
//Einde r1, distanceparameter
}}






if(blokgrootte >0){

    int orde_matrix=blokgrootte;
if(t1==0){//LEEG Toevoegen



    SDPwriter.write("#\n");

    SDPwriter.write("# blok "+ bloknr + " = ("+ mu1 +", " +t1 +")\n");  
    System.out.println("# blok "+ bloknr + " = ("+ mu1 +", " +t1 +")\n");

    SDPwriter.write("# row 1"+ " "+"column 1"+ "\n");
    SDPwriter.write("1,");	

    for (int bovenindex=1; bovenindex<= orde_matrix; bovenindex++){    


    SDPwriter.write(bovenmatrix[bovenindex]);  
    if (bovenindex < orde_matrix) {
    SDPwriter.write(",");
    }   
    else{SDPwriter.write(";\n");}
    }	

    for (int rowindex=1; rowindex<= orde_matrix; rowindex++){
    for (int colindex=1; colindex<= orde_matrix; colindex++){
    
    if(colindex==1){
     SDPwriter.write("# row "+ (rowindex+1) + " "+"column "+  1+ "\n");
    SDPwriter.write(",\n");
    }

    SDPwriter.write("# row "+ (rowindex+1) + " "+"column "+  (colindex+1)+ "\n");
    SDPwriter.write(polymatrix[rowindex][colindex]);  
 
    if (colindex < orde_matrix) {
    SDPwriter.write(",\n");
    }
    }
    if (rowindex < orde_matrix) {
    SDPwriter.write(";\n");
    }
    }
    SDPwriter.write(">= 0\n");


}
else{




    SDPwriter.write("#\n");

    SDPwriter.write("# blok "+ bloknr + " = ("+ mu1 +", " +t1 +")\n");  
    System.out.println("# blok "+ bloknr + " = ("+ mu1 +", " +t1 +")\n");


    for (int rowindex=1; rowindex<= orde_matrix; rowindex++){
    for (int colindex=1; colindex<= orde_matrix; colindex++){
    SDPwriter.write("# row "+ rowindex + " "+"column "+  colindex+ "\n");

    SDPwriter.write(polymatrix[rowindex][colindex]);  
   
    if (colindex < orde_matrix) {
    SDPwriter.write(",\n");
    }
    }
    if (rowindex < orde_matrix) {
    SDPwriter.write(";\n");
    }
    }
    SDPwriter.write(">= 0\n");

} 

}






} //EINDE C=leeg, tweetallen.











//
// We doen het geval |C| = 1, TRIPLES (IN THE ARTICLE C IS CALLED "D")
//
    SDPwriter.write("# |C|=1, drietallen\n");
//
mu1=w;
int mu2=n-w;
//mu1=grootte eerste tableau, mu2=grootte tweede tableau.

//shape van het eerste tableau van hoogte 2
for ( int t1 =0; t1<=mu1/2;t1++){

//shape van het tweede tableau van hoogte 2
for ( int t2 =0; t2<=mu2/2;t2++){






String startP1pol=" 1 ";
for(int s=1; s<=mu1-2*t1; s=s+1){
startP1pol=maal_pol(startP1pol," 1 x11");
}
for(int s=1; s<=t1; s=s+1){
startP1pol=maal_pol(startP1pol,det2);
}


String startP2pol=" 1 ";
for(int s=1; s<=mu2-2*t2; s=s+1){
startP2pol=maal_pol(startP2pol," 1 x11");
}
for(int s=1; s<=t2; s=s+1){
startP2pol=maal_pol(startP2pol,det2);
}









bloknr++;
int blokgrootte = blocksize(mu1,mu2,t1,t2,d,w);



String[][] polymatrix = new String[blokgrootte+1][blokgrootte+1];




System.out.println("de grootte van blok " + bloknr + ", het blok corresponderend met de partitie, ("+ mu1 +", " +t1 + ", " +mu2  + ", "+t2+") wordt: "+  blokgrootte);





int row=1; int col=1;


//content van rijtableau,1
for ( int r1 =mu1-t1; r1>=t1;r1--){

//content van rijtableau,2
for ( int r3 =mu2-t2; r3>=t2;r3--){

int e11 = r1;
int e12 = mu1-r1;
int f11 = r3;
int f12 = mu2-r3;

if((e12+f12)==w && (  (e11+f12) == 0 ||  (e11+f12)>=d )    ){


//content van kolomtableau,1
for ( int r2 =mu1-t1; r2>=t1;r2--){

//content van kolomtableau,2
for ( int r4 =mu2-t2; r4>=t2;r4--){

int e21 = r2;
int e22 = mu1-r2;
int f21 = r4;
int f22 = mu2-r4;



if((e22+f22)==w && (  (e21+f22) == 0 ||  (e21+f22)>=d )    ){


if(col >= row){


//MAAKPOLYNOOM


String temppol1= zetom_pol( mk_poleentab(mu1, t1, r1, r2, startP1pol), "5");



String temppol2= zetom_pol( mk_poleentab(mu2, t2, r3, r4, startP2pol), "6");


String temppolmult= maal_pol(temppol1,temppol2);




String finalpol = linear(temppolmult);

polymatrix[row][col]= finalpol;    
}else{polymatrix[row][col]= "";    }









col++;
//Einde col>=row


//Einde r2,r4, distanceparameter2
}}}


col=1; 





row++;
//Einde r1,r3, distanceparameter
}}}











if(blokgrootte >0){






    int orde_matrix=blokgrootte;


    SDPwriter.write("#\n");

    SDPwriter.write("# blok "+ bloknr + " = ("+ mu1 +", " +t1 + ", " +mu2  + ", "+t2+")\n");  
    System.out.println("# blok "+ bloknr + " = ("+ mu1 +", " +t1 + ", " +mu2  + ", "+t2+")\n");


    for (int rowindex=1; rowindex<= orde_matrix; rowindex++){
    for (int colindex=1; colindex<= orde_matrix; colindex++){
    SDPwriter.write("# row "+ rowindex + " "+"column "+  colindex+ "\n");

    SDPwriter.write(polymatrix[rowindex][colindex]);  
   
    if (colindex < orde_matrix) {
    SDPwriter.write(",\n");
    }
    }
    if (rowindex < orde_matrix) {
    SDPwriter.write(";\n");
    }
    }
    SDPwriter.write(">= 0\n");

}


}
}








//ADD |D|=2 QUADRUPLES


      	for (int t=d; t<=n; t++){
		String mon="";
		if(t >=d && t%2==0  && t/2+w <=n && t <=2*w)	{
			// Maakblok. 111111(w)0000000
			//           0001111111(w)000  dist t.

		mu1=t/2;
		mu2=w-t/2;
		int mu3= t/2;
		int mu4=n-w-t/2;


		//mu1=grootte eerste tableau, mu2=grootte tweede tableau, mu3=grootte derde tableau, mu4=grootte vierde tableau.

		//shape van het eerste tableau van hoogte 2
		for ( int t1 =0; t1<=mu1/2;t1++){

		//shape van het tweede tableau van hoogte 2
		for ( int t2 =0; t2<=mu2/2;t2++){

		//shape van het derde tableau van hoogte 2
		for ( int t3 =0; t3<=mu3/2;t3++){

		//shape van het vierde tableau van hoogte 2
		for ( int t4 =0; t4<=mu4/2;t4++){

		



		String startP1pol=" 1 ";
		for(int s=1; s<=mu1-2*t1; s=s+1){
			startP1pol=maal_pol(startP1pol," 1 x11");
		}
		for(int s=1; s<=t1; s=s+1){
		startP1pol=maal_pol(startP1pol,det2);
		}


			String startP2pol=" 1 ";
		for(int s=1; s<=mu2-2*t2; s=s+1){
			startP2pol=maal_pol(startP2pol," 1 x11");
		}
		for(int s=1; s<=t2; s=s+1){
			startP2pol=maal_pol(startP2pol,det2);
		}

		String startP3pol=" 1 ";
		for(int s=1; s<=mu3-2*t3; s=s+1){
			startP3pol=maal_pol(startP3pol," 1 x11");
		}
		for(int s=1; s<=t3; s=s+1){
		startP3pol=maal_pol(startP3pol,det2);
		}


			String startP4pol=" 1 ";
		for(int s=1; s<=mu4-2*t4; s=s+1){
			startP4pol=maal_pol(startP4pol," 1 x11");
		}
		for(int s=1; s<=t4; s=s+1){
			startP4pol=maal_pol(startP4pol,det2);
		}




		bloknr++;
		int blokgrootte = blocksize2(mu1,mu2,mu3,mu4,t1,t2,t3,t4,d,w);
			String[][] polymatrix = new String[blokgrootte+1][blokgrootte+1];

	
		System.out.println("de grootte van blok " + bloknr + ", het blok corresponderend met de partitie, ("+ mu1 +", " +t1 + ", " 					+mu2  + ", "+t2+") wordt: "+  blokgrootte);


		int row=1; int col=1;


		for (int r1=t1; r1<=mu1-t1; r1++){
		for (int r2=t2; r2<=mu2-t2; r2++){
		for (int r3=t3; r3<=mu3-t3; r3++){
		for (int r4=t4; r4<=mu4-t4; r4++){








		//NU BEKIJKEN WE DE DISTANCE&WEIGHT-CONDITIE VOOR TABLEAU SIGMA
		int e11=r1;
		int e12=mu1-r1;
		int f11=r2;
		int f12=mu2-r2;


		int g11=r3;
		int g12=mu3-r3;
		int h11=r4;
		int h12=mu4-r4;

		int dac = e11+f11+g12+h12 ;
      	 	int dbc = e12+f11+g11+h12 ;
		
     	 	int weightg = e12+f12+g12+h12 ;
		
		if(  ((dac ==0 || dac >=d) )&&    (dbc==0 || dbc >=d)   && weightg==w       )    {


		for (int r21=t1; r21<=mu1-t1; r21++){
		for (int r22=t2; r22<=mu2-t2; r22++){
		for (int r23=t3; r23<=mu3-t3; r23++){
		for (int r24=t4; r24<=mu4-t4; r24++){



		//NU BEKIJKEN WE DE DISTANCE&WEIGHT-CONDITIE VOOR TABLEAU SIGMA
		int e211=r21;
		int e212=mu1-r21;
		int f211=r22;
		int f212=mu2-r22;


		int g211=r23;
		int g212=mu3-r23;
		int h211=r24;
		int h212=mu4-r24;

		int d2ac = e211+f211+g212+h212 ;
      	 	int d2bc = e212+f211+g211+h212 ;
		
     	 	int weight2g = e212+f212+g212+h212 ;
		
		if(  ((d2ac ==0 || d2ac >=d) )&&    (d2bc==0 || d2bc >=d)   && weight2g==w       )    {




		if(col >= row){


		//MAAKPOLYNOOM



		String temppol1= zetom_pol( mk_poleentab(mu1, t1, r1, r21, startP1pol), "1");

		String temppol2= zetom_pol( mk_poleentab(mu2, t2, r2, r22, startP2pol), "2");

		String temppol3= zetom_pol( mk_poleentab(mu3, t3, r3, r23, startP3pol), "3");

		String temppol4= zetom_pol( mk_poleentab(mu4, t4, r4, r24, startP4pol), "4");


		String temppolmult= maal_pol(maal_pol(maal_pol(temppol1,temppol2),temppol3),temppol4);




		String finalpol = linear(temppolmult);

		polymatrix[row][col]= finalpol;    
		  
		}else{polymatrix[row][col]= "";    }









		col++;
		//Einde col>=row
		




		}
		}}}}  // EIND FOR-FILLINGS TUPLE2.
		col=1; 









		row++;

		}		
		}}}}  // EIND FOR-FILLINGS TUPLE 1





		if(blokgrootte >0){






		    int orde_matrix=blokgrootte;

		    SDPwriter.write("#\n");

		    SDPwriter.write("# blok "+ bloknr +  " = ("+ mu1 +", " +t1 + ", " +mu2  + ", "+t2+", "+ mu3 +", " +t3 + ", " +mu4  + ", "+t4+")\n");  
		    System.out.println("# blok "+ bloknr + " = ("+ mu1 +", " +t1 + ", " +mu2  + ", "+t2+", "+ mu3 +", " +t3 + ", " +mu4  + ", "+t4+")\n");


		    for (int rowindex=1; rowindex<= orde_matrix; rowindex++){
		    for (int colindex=1; colindex<= orde_matrix; colindex++){
		    SDPwriter.write("# row "+ rowindex + " "+"column "+  colindex+ "\n");

		    SDPwriter.write(polymatrix[rowindex][colindex]);  
		   
		    if (colindex < orde_matrix) {
		    SDPwriter.write(",\n");
		    }
		    }
		    if (rowindex < orde_matrix) {
		    SDPwriter.write(";\n");
		    }
		    }
		    SDPwriter.write(">= 0\n");
		}









		}}}}// EIND FOR OVER SHAPES


		}
	}


SDPwriter.write("# nonnegativity\n");
for (int i=1;i<=nummer ;i++){
        int ynummer=i;
	SDPwriter.write("y"+ynummer + " >= 0\n");
}



//Hier eindigt het schrijven van het .sdp-bestand. We moeten een aantal mogelijke excepties afhandelen omdat java dat zo wil.
}catch (IOException e) { } 
finally {
	if (SDPwriter != null) {
                try {
                    SDPwriter.close(); 
                } catch (Exception e) { }
	}
} // Het .sdp-bestand is nu afgemaakt


}  //Bij dit }-teken eindigt het programma.
/*
//
//--------------------------------------------   
// Hieronder staan alle gebruikte subroutines.
//--------------------------------------------
//
//
*/


/* 
//
//-------------------------------------------------------------------------------
// Subroutine verwerk. Hier wordt een monoom (in x-variabelen) verwerkt: aan een monoom wordt het nummer van zijn ortbit toegekend.
//
//
*/



	static void verwerk(String mon){
		nummers.put(mon,nummer);
		//System.out.println("" + mon+ " "+nummer); 
                //LOKALE VARIABELEN, DAAROM ZIJN monab, monABCD, monacc niet globaal als class-variabele gedefinieerd.
		String monab = monomen.get(mon)[0];if (nummers.get(monab)==null ||nummers.get(monab)==0) {verwerk(monab);}
		String monABCD = monomen.get(mon)[1];if (nummers.get(monABCD)==null ||nummers.get(monABCD)==0) {verwerk(monABCD);}
		String mon01 = monomen.get(mon)[2];if (nummers.get(mon01)==null ||nummers.get(mon01)==0) {verwerk(mon01);}
		if (monomen.get(mon).length==4) {
			monacc= monomen.get(mon)[3];
			if (nummers.get(monacc)==null || nummers.get(monacc)==0) {
				verwerk(monacc);
			}
		}	
	
	}



/*
#
# ---------------------------------------------------------------------------
# OPTELLING EN VERMENIGVULDIGING VAN POLYNOMEN
#
# SUBROUTINE OM TWEE MONOMEN TE VERMENIGVULDIGEN
# (monomen gegeven door "x...x...x..." , waarbij ... elke willekeurige string
# zonder x'en kan zijn (willekeurig lang ook)))
#
*/
    static String maal_mon(String mona, String monb){

        Map<String, Integer> mult = new TreeMap<String, Integer>();

	String[] e = mona.split("x");
	String[] f = monb.split("x");

	for (int i=1; i<e.length; i++){
		if(mult.get(e[i])==null){
		    mult.put(e[i],1);
		} 
		else{
		mult.put(e[i], mult.get(e[i])+1);
		}
	}

	for (int i=1; i<f.length; i++){
		if(mult.get(f[i])==null){
			mult.put(f[i], 1);
		}
		else{
			mult.put(f[i], mult.get(f[i])+1);
		}
	}
	StringBuilder lokaalmonoombuilder=new StringBuilder();


 	for (Map.Entry<String, Integer> varmetmult : mult.entrySet()){
    	String var = varmetmult.getKey();
		for (int i=1; i<= mult.get(var); i++) {
			lokaalmonoombuilder.append("x"); lokaalmonoombuilder.append(var);
		}
	}
	return lokaalmonoombuilder.toString();
    }


/*
#
#
# SUBROUTINE OM TWEE POLYNOMEN TE VERMENIGVULDIGEN
# (polynomen gegeven door " coefficient monoom coefficient monoom ... coefficient monoom")
#
# Met een constante vermenigvuldigen kan ook, specificeer de constante dan als " constante c";
#
*/
    static String maal_pol(String pola, String polb){

        Map<String, BigInteger> coef = new TreeMap<String, BigInteger>();
	String monoom;
	String[] e = pola.split(" ");
	String[] f = polb.split(" ");

	// Als de lengte 2 is is pola een constante
	if(e.length==2){
		String[] temp = new String[3] ;
			for(int k=0; k<2; k++){ temp[k]=e[k]; 
			} 
		temp[2]= "c";
		e=temp;
	}
	// Als de lengte 2 is is polb een constante
	if(f.length==2){
		String[] temp = new String[3]; 
			for(int k=0; k<2; k++){ temp[k]=f[k]; 
			} 
		temp[2]= "c";
		f=temp;
	}


	for (int i=1; i<e.length; i=i+2){
		for (int j=1; j<f.length; j=j+2){
			monoom="";
			if(j+1 < f.length && i+1 < e.length){
			    monoom = maal_mon(e[i+1],f[j+1]);
			}
			else if (j+1 == f.length && i+1 < e.length ) {
			    monoom = maal_mon(e[i+1],"");
			}
			else if (j+1 < f.length && i+1 == e.length ) {
			    monoom = maal_mon(f[j+1],"");
			}
			else if (j+1 == f.length && i+1 == e.length ) {
			    monoom = maal_mon("","");
			}
			
			if(coef.get(monoom)==null){
			coef.put(monoom, (new BigInteger(e[i])).multiply(new BigInteger(f[j])));
			}
			else{
			coef.put(monoom, coef.get(monoom).add(new BigInteger(e[i]).multiply(new BigInteger(f[j]))));
			}
		}
	}


	StringBuilder polynoombuilder=new StringBuilder();

 	for (Map.Entry<String, BigInteger> coefvar : coef.entrySet()){
    	String mon = coefvar.getKey();
		if (!coef.get(mon).equals(BigInteger.ZERO)) {
			polynoombuilder.append(" "); polynoombuilder.append(coef.get(mon)); polynoombuilder.append(" "); polynoombuilder.append(mon);
		}
	}
	return polynoombuilder.toString();
    }


/*
#
#
# SUBROUTINE OM EEN POLYNOOM MET EEN MONOOM (WAARBIJ WE AANNEMEN DAT DE CONSTANTE 1 IS) TE VERMENIGVULDIGEN
# (polynomen gegeven door " coefficient monoom coefficient monoom ... coefficient monoom")
#
*/
    static String maal_polmon(String pola, String monb){

        Map<String, Integer> coef = new TreeMap<String, Integer>();
	String[] e = pola.split(" ");

	// Als de lengte 2 is is pola een constante
	if(e.length==2){
		String[] temp = new String[3] ;
			for(int k=0; k<2; k++){ temp[k]=e[k]; 
			} 
		temp[2]= "c";
		e=temp;
	}
	
	StringBuilder polynoombuilder=new StringBuilder();

	for (int i=1; i<e.length; i=i+2){

		e[i+1]=maal_mon(e[i+1],monb);
		polynoombuilder.append(" "); polynoombuilder.append(e[i]); polynoombuilder.append(" "); polynoombuilder.append(e[i+1]);
	}

	return polynoombuilder.toString();
    }




/*
#
#
# SUBROUTINE OM TWEE POLYNOMEN OP TE TELLEN
# (polynomen gegeven door " coefficient monoom coefficient monoom ... coefficient monoom")
#
*/
    static String plus_pol(String pola, String polb){

        Map<String, BigInteger> coef = new TreeMap<String, BigInteger>();
	String monoom;
	String[] e = pola.split(" ");
	String[] f = polb.split(" ");

	for (int i=1; i<e.length; i=i+2){
		monoom = e[i+1];
		if(coef.get(monoom)==null){
			coef.put(monoom, new BigInteger(e[i]));
		}
		else{
			coef.put(monoom, coef.get(monoom).add(new BigInteger(e[i])));
		}
	}


	for (int j=1; j<f.length; j=j+2){
		monoom = f[j+1];
		if(coef.get(monoom)==null){
			coef.put(monoom, new BigInteger(f[j]));
		}
		else{
			coef.put(monoom, coef.get(monoom).add(new BigInteger(f[j])));
		}
	}
	StringBuilder polynoombuilder=new StringBuilder();
 	for (Map.Entry<String, BigInteger> coefvar : coef.entrySet()){
    	String mon = coefvar.getKey();
		if (!coef.get(mon).equals(BigInteger.ZERO)) {
			polynoombuilder.append(" "+coef.get(mon)); polynoombuilder.append(" " + mon);
		}
	}
	return polynoombuilder.toString();
    }

/*
# SUBROUTINE OM POLYNOOM DOOR CONSTANTE TE DELEN
# (polynomen gegeven door " coefficient monoom coefficient monoom ... coefficient monoom")
#(kan ook met maal_pol maar op deze manier lijkt er minder te worden afgerond)
#DIT IS INTEGER DIVISION, ALLE COEFFICIENTEN MOETEN WEL DEELBAAR ZIJN DOOR DE CONSTANTE
#*/
    static String deeldoorconstante_pol(String pola, BigInteger constante){
        Map<String, BigInteger> coef = new TreeMap<String, BigInteger>();
	String monoom;
	String[] e = pola.split(" ");

	for (int i=1; i<e.length; i=i+2){
		if(i+1 == e.length){
		    monoom = "";
		}else{ monoom = e[i+1];}
		if(coef.get(monoom)==null){
			coef.put(monoom, (new BigInteger(e[i])).divide(constante));
		}
		else{
			coef.put(monoom, coef.get(monoom).add((new BigInteger(e[i])).divide(constante)));
		}
	}
	StringBuilder polynoombuilder=new StringBuilder();
 	for (Map.Entry<String, BigInteger> coefvar : coef.entrySet()){
    	String mon = coefvar.getKey();
		if (!coef.get(mon).equals(BigInteger.ZERO)) {
			polynoombuilder.append(" "+coef.get(mon)); polynoombuilder.append(" "); polynoombuilder.append(mon);
		}
	}
	return polynoombuilder.toString();
    }

/*
#
#
# SUBROUTINE OM EEN POLYNOOM AF TE LEIDEN NAAR EEN VARIABELE 
# */

	//Differentieer naar diffvar en vermenigvuldig met varmultiply. VOER DE varmultpoly in ZONDER x.

    static String diffmult_pol(String pol, String diffvar, String varmultiply){

        Map<String, BigInteger> coef = new TreeMap<String, BigInteger>();
	String[] e = pol.split(" ");
	int macht;
	boolean algedaan;
	String monoom;
	String var;
        Map<String, Integer> mult;

	for (int i=1; i<e.length; i=i+2){
		macht=0;
		algedaan=false;
		monoom = e[i+1];

        	mult = new TreeMap<String, Integer>();

		String[] f = monoom.split("x");
		for (int j=1; j<f.length; j++){
			if(mult.get(f[j])==null){
				mult.put(f[j],1);
			}
			else{
				mult.put(f[j],mult.get(f[j])+1);
			}
		}
			if(mult.get(varmultiply)==null){
				mult.put(varmultiply,1);
			}
			else{
				mult.put(varmultiply,mult.get(varmultiply)+1);
			}
		StringBuilder monoomlokaalbuilder= new StringBuilder();
		var="";
 		for (Map.Entry<String, Integer> multvar : mult.entrySet()){
    			var = multvar.getKey();
			for (int k=1;k<=mult.get(var);k++) {
				if(var.equals(diffvar)){
                                    macht = macht+1;
                                    }
				if(algedaan==false && (var).equals(diffvar)){	
					algedaan=true;			
				}else{monoomlokaalbuilder.append("x"); monoomlokaalbuilder.append(var);}
			}
			mult.put(var,null);

		}
		monoom=monoomlokaalbuilder.toString();	
		if(algedaan==true) {
			if(coef.get(monoom)==null){
				coef.put(monoom, (new BigInteger(e[i])).multiply(BigInteger.valueOf(macht)));
			}
			else{
				coef.put(monoom, coef.get(monoom).add(new BigInteger(e[i])).multiply(BigInteger.valueOf(macht)));
			}
		}
		//System.out.println("monoom in diff = " + monoom);
	}	
	StringBuilder polynoombuilder=new StringBuilder();
 	for (Map.Entry<String, BigInteger> coefvar : coef.entrySet()){
    	String mon = coefvar.getKey();
		if (!coef.get(mon).equals(BigInteger.ZERO)) {
			polynoombuilder.append(" "+coef.get(mon)); polynoombuilder.append(" "); polynoombuilder.append(mon);
		}
	}
	return polynoombuilder.toString();
    }

/*#
# SUBROUTINE dij_pol. Past de operator dij uit Dions artikel toe op een polynoom.
# */

   static String dij_pol(String pol, int ivar, int jvar){
	String qol="";
	for (int s=1; s<=4;s=s+1){
		qol=plus_pol(qol,diffmult_pol(pol,(""+jvar + s), (""+ivar+ s )));
	}		
	return qol;
   }


/*#
# SUBROUTINE dijstar_pol. Past de operator dij* uit Dions artikel toe op een polynoom.
# */

   static String dijstar_pol(String pol, int ivar, int jvar){
	String qol="";
	for (int s=1; s<= 4;s=s+1){
		qol=plus_pol(qol,diffmult_pol(pol,(""+s+ivar),(""+s+jvar) ));							
	}		
	return qol;
   }


/*
#----------------------------------------------------------------------------------------
# SUBROUTINE OM HET POLYNOOM  p_{sigma,tau}  TE BEPALEN  
# 
*/

    static String mk_pol(String basisvorm,String eindpol, int l1, int l2, int l3, int l4, int m11, int m12, int m13, int g11, int g12, int b1, int m21, int m22, int m23, int g21, int g22, int b2){ 


	int[][] D1 = new int[5][5];
	int[][] D2 = new int[5][5]; 


	// D1[i][j] = aantal symbolen i in rij j van tableau sigma 
	D1[1][1]=b1;
	D1[2][1]=g11-b1;
	D1[2][2]=g12;
	D1[3][1]=m11-g11;
	D1[3][2]=m12-g12;
	D1[3][3]=m13;
	D1[4][1]=l1-m11;
	D1[4][2]=l2-m12;
	D1[4][3]=l3-m13;
	D1[4][4]=l4;




	// D2[i][j] = aantal symbolen i in rij j van tableau tau
	D2[1][1]=b2;
	D2[2][1]=g21-b2;
	D2[2][2]=g22;
	D2[3][1]=m21-g21;
	D2[3][2]=m22-g22;
	D2[3][3]=m23;
	D2[4][1]=l1-m21;
	D2[4][2]=l2-m22;
	D2[4][3]=l3-m23;
	D2[4][4]=l4;

        /*
	// Maak het polynoom uit de basisvorm */

	String P1 = basisvorm; 
	BigInteger deler = new BigInteger("1");

	for(int j=3; j>=1; j--){
		for(int i=j+1; i<=4; i++){
			for(int k=1;k<=(D2[i][j]);k=k+1){
				P1=dijstar_pol(P1,j,i);
                		deler=deler.multiply(BigInteger.valueOf(k));
			}       //     System.out.println("i: "+ i + "j: "+j + "djistar : "+ P1);
			for(int k=1;k<=(D1[i][j]);k=k+1){
				P1=dij_pol(P1,i,j);  
                		deler=deler.multiply(BigInteger.valueOf(k));
			}        //    System.out.println("i: "+ i + "j: "+j + "dij : "+ P1);  
		}
	}
       			P1 = deeldoorconstante_pol(P1,deler);  

	//#vermenigvuldig nog met (4-determinant)^(l[4]);
	//P1=maal_pol(P1,eindpol);
	return P1;	//polynoom is klaar
    }


/*
#-------------------------------------------------------------------------------
#
# SUBROUTINE DIE ELK POLYNOOM IN VARIABELEN x$partitie  OMZET IN EEN LINEAIRE FUNCTIE
# IN VARIABELEN y$nummer.
#
*/
   static String linear(String pol){
	String[] e = pol.split(" ");
        Map<Integer, BigInteger> coefnummer = new TreeMap<Integer, BigInteger>();
	int lokaalnummer;


	for (int i=1; i<e.length; i=i+2){
		if(nummers.get(e[i+1])!=null){
			lokaalnummer = nummers.get(e[i+1]);
			
			if(coefnummer.get(lokaalnummer) ==null){
				coefnummer.put(lokaalnummer,new BigInteger(e[i]));
			}
			else{
				coefnummer.put(lokaalnummer,coefnummer.get(lokaalnummer).add(new BigInteger(e[i])));
			}

		}
	}

	StringBuilder finlin= new StringBuilder();
	int nieuwnum;

 	for (Map.Entry<Integer, BigInteger> nummeriterator : coefnummer.entrySet()){
    	lokaalnummer = nummeriterator.getKey();
		if (!coefnummer.get(lokaalnummer).equals(BigInteger.ZERO)) {
                        nieuwnum=lokaalnummer;
			finlin.append(" +"+coefnummer.get(lokaalnummer)); finlin.append(" y"+ nieuwnum);
		}
	}
	return finlin.toString();
   }


/*
#
#
#dezelfde linear methode om de bovenste rij mee te bepalen.
#
*/
   static String linearleeg(String pol){
	String[] e = pol.split(" ");
        Map<Integer, Integer> coefnummer = new TreeMap<Integer, Integer>();
	int lokaalnummer;


	for (int i=1; i<e.length; i=i+2){
		if(nummers.get(e[i+1])!=null){
			lokaalnummer = nummers.get(e[i+1]).intValue();
			
			if(coefnummer.get(lokaalnummer) ==null){
				coefnummer.put(lokaalnummer,leegcoef.get(lokaalnummer));
			}
			else{
				coefnummer.put(lokaalnummer,leegcoef.get(lokaalnummer)+Integer.parseInt(e[i]));
			}

		}
	}

	StringBuilder finlin= new StringBuilder();
	int nieuwnum;

 	for (Map.Entry<Integer, Integer> nummeriterator : coefnummer.entrySet()){
    	lokaalnummer = nummeriterator.getKey();
		if (coefnummer.get(lokaalnummer)!=0) {
                        nieuwnum=lokaalnummer;
			finlin.append(" +"+coefnummer.get(lokaalnummer)); finlin.append(" y"+ nieuwnum);
		}
	}
	return finlin.toString();
   }

/*
#-------------------------------------------------------------------------------
#
# SUBROUTINE DIE DE GROOTTE VAN EEN BLOK BEPAALT
#
*/
   static int blocksize(int mu1, int mu2, int t1, int t2, int d, int w){
	int n=mu1+mu2;
	int grootte =0; 

	// (m11, m12, m13) is de shape van tableau sigma als je 4-en weghaalt 
	for (int r1=t1; r1<=mu1-t1; r1++){
	for (int r2=t2; r2<=mu2-t2; r2++){

	int e11 = r1;
	int e12 = mu1-r1;
	int f11 = r2;
	int f12 = mu2-r2;

	if((e12+f12)==w && (  (e11+f12) == 0 ||  (e11+f12)>=d )    ){	


		grootte++; 
	}
	}}

	return grootte;
   }

/*
#-------------------------------------------------------------------------------
#
# SUBROUTINE DIE DE GROOTTE VAN EEN BLOK BEPAALT VOOR C=LEEG, TWEETALLEN
#
*/
   static int blocksizeleeg(int mu1,  int t1, int d, int w){
	int n=mu1;
	int grootte =0; 

	// (m11, m12, m13) is de shape van tableau sigma als je 4-en weghaalt 
	for (int r1=t1; r1<=mu1-t1; r1++){

	int e11 = r1;
	int e12 = mu1-r1;


	if(e12==w){	


		grootte++; 
	}
	}

	return grootte;
   }

/* #
#
# SUBROUTINE OM EEN MONOOM IN VARIABELEN xijk OM TE ZETTEN IN POLYNOOM IN VARIABELEN x_partitie
#*/

 
  static String zetom_mon(String mon, String component){
	String[] e=mon.split("x");
	String localpol=" 1 ";
	String localtijpol="";
	for (int i=1; i< e.length ;i++){
        localtijpol =formules.get(component+""+e[i]);
	localpol=maal_pol(localtijpol,localpol);
	}
	return localpol;
}

  static String zetomin01_mon(String mon){
	String[] e=mon.split("x");
	String localtijpol="";
	String localpol="";
	

	for (int i=1; i< e.length ;i++){
        localtijpol =partmon.get(e[i]);
	localpol= localpol +""+partmon.get(e[i]);
	}
	return localpol;
}


/*#
# SUBROUTINE OM EEN POLYNOOM OM TE ZETTEN VAN VARIABELEN xijk NAAR x_partitie
#*/

  static String zetom_pol(String inputpol, String comp){
	String[] e=inputpol.split(" ");
	String outputpol="";
	String tijpol;
	String factor;

	for (int i=1;i<e.length-1;i=i+2){
		tijpol=zetom_mon(e[i+1],comp);
		factor=" "+e[i]+" ";
		tijpol=maal_pol(factor,tijpol);
		outputpol=plus_pol(tijpol,outputpol);
	}
	if(outputpol.equals("")){outputpol=" 1 ";}

	return outputpol;
}

/*
#-------------------------------------------------------------------------------
#
# SUBROUTINE DIE DE GROOTTE VAN EEN BLOK BEPAALT voor |C|=2
#
*/
   static int blocksize2(int mu1, int mu2, int mu3, int mu4, int t1, int t2,int t3, int t4, int d, int w){
	int n=mu1+mu2+mu3+mu4;
	int grootte =0; 


	for (int r1=t1; r1<=mu1-t1; r1++){
	for (int r2=t2; r2<=mu2-t2; r2++){
	for (int r3=t3; r3<=mu3-t3; r3++){
	for (int r4=t4; r4<=mu4-t4; r4++){


	//NU BEKIJKEN WE DE DISTANCE-CONDITIE VOOR TABLEAU SIGMA
	int e11=r1;
	int e12=mu1-r1;
	int f11=r2;
	int f12=mu2-r2;


	int g11=r3;
	int g12=mu3-r3;
	int h11=r4;
	int h12=mu4-r4;

	int dac = e11+f11+g12+h12 ;
        int dbc = e12+f11+g11+h12 ;

        int weightg = e12+f12+g12+h12 ;

	if(  ((dac ==0 || dac >=d) )&&    (dbc==0 || dbc >=d)   && weightg==w       )    {


		grootte++; 
	}
	


	}}}}

	return grootte;
   }



/*
#----------------------------------------------------------------------------------------
# SUBROUTINE OM HET POLYNOOM  p_{sigma,tau} HOOGTE2  TE BEPALEN  OP DE DIFF-MANIER
# 
*/

    static String mk_poleentab(int mu1, int t1, int r1, int r2, String startP1pol){ 



	int[][] D1 = new int[3][3];
	int[][] D2 = new int[3][3]; 


	// D1[i][j] = aantal symbolen i in rij j van tableau sigma,1 
	D1[1][1]=r1;
	D1[2][1]=mu1-t1-r1;
	D1[2][2]=t1;



	// D2[i][j] = aantal symbolen i in rij j van tableau tau,1
	D2[1][1]=r2;
	D2[2][1]=mu1-t1-r2;
	D2[2][2]=t1;


	// Maak het polynoom uit de basisvorm */

	String P1 = startP1pol; 
	BigInteger deler = new BigInteger("1");

	for(int j=1; j>=1; j--){
		for(int i=j+1; i<=2; i++){
			for(int k=1;k<=(D2[i][j]);k=k+1){
				P1=dijstar_pol(P1,j,i);
                		deler=deler.multiply(BigInteger.valueOf(k));
			}       //     System.out.println("i: "+ i + "j: "+j + "djistar : "+ P1);
			for(int k=1;k<=(D1[i][j]);k=k+1){
				P1=dij_pol(P1,i,j);  
                		deler=deler.multiply(BigInteger.valueOf(k));
			}        //    System.out.println("i: "+ i + "j: "+j + "dij : "+ P1);  
		}
	}
       	P1 = deeldoorconstante_pol(P1,deler);  
	return P1;
    }

/*
#
#------------------------------------------------------------------------------------------
#
# Subroutine om binomiaalcoefficienten te berekenen
#
#
*/
    static BigInteger binomial(int n, int k)
    {
        if (k>n-k)
            k=n-k;
 
        BigInteger b= new BigInteger("1");
        for (int i=1, m=n; i<=k; i++, m--)
            b= (b.multiply(BigInteger.valueOf(m))).divide(BigInteger.valueOf(i));
        return  b;
    }
 
   
}



