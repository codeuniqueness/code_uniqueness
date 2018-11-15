#! /usr/bin/perl
no warnings 'uninitialized';
#WRITTEN BY A. SCHRIJVER
sub splitsaf{
    if ($string =~ /^([\+\-\,\;\>])(.*)$/){
      $af=$1;
      $string=$2;
    }
    elsif ($string =~ /^([A-Za-z][^\,^\;^\>^\+^\-]*)([\,\;\>\+\-].*)$/ ) {
      $af=$1;
      $string=$2;
    }
    elsif ($string =~ /^([0-9\.]+[Ee][\+\-][0-9][0-9])([^0-9].*)$/ ) {
      $af=$1;
      $string=$2;
    }
    elsif ($string =~ /^([0-9\.]+[Ee][\+\-][0-9][0-9])$/ ) {
      $af=$1;
      $string="";
    }
    elsif ($string =~ /^([0-9\.]+)([^0-9\.].*)$/ ) {
      $af=$1;
      $string=$2;
    }
    else {
       $af=$string;
       $string="";
    }
}
open (INP,"$ARGV[0]\.sdp");
open (OUTP,">$ARGV[0]\.dat-s");
$teken=1;
$factor=1;
$tekenaan=0;
$factoraan=0;
while (<INP>){
$regelnr++;
if ($_ !~ /^[\#]/) {
   @e=split;
   $regel=$_;
   $relevant=1;
   if ("\L$e[0]" eq "maximize") {
      $fase=1;
      $min=-1;
   }
   elsif ("\L$e[0]" eq "minimize") {
      $fase=1;
      $min=+1;
   }
   elsif ("\L$e[0]" =~ /^subject/) {
      $fase=2;
      if ($factoraan) { die "Line $regelnr incorrect\n";}
      if ($tekenaan) { die "Line $regelnr incorrect\n";}
      $block=1;
      $row=1;
      $column=1;
   }
   elsif ($fase==1){
      for ($i=0;$i<=$#e;$i++) {
	 $string=$e[$i];
	 while ($string ne "") {
	    &splitsaf;
	    if ($af eq "+") {
	       $teken*=+1; $factor= 1;
	       $tekenaan=1;
	    }
	    elsif ($af eq "-") {
	       $teken*=-1; $factor= 1;
	       $tekenaan=1;
	    }
	    elsif ($af =~ /^[0-9]/) {
	       $factor = $af;
	       $factoraan=1;
	    }
	    elsif ($af =~ /^[A-Za-z]/) {
	       if ($bekend{$af} eq "") {
		  $avar++;
		  $bekend{$af} = $avar;
		  print "$avar $af\n";
	       }
	       $optcoef[$bekend{$af}]+= $min * $teken * $factor;
	       $tekenaan=0; $factoraan=0; $factor=1; $teken=1;
	    }
	    else {
	       die "Error Line $regelnr: $regel af=$af string=$string\n";
	    }
	 }
      }
   }
   elsif ($fase==2){
      for ($i=0;$i<=$#e;$i++) {
	 $string=$e[$i];
	 while ($string ne "") {
	    &splitsaf;
	    if ($relevant==0) {
	    }
	    elsif ($af eq "+") {
	       if ($factoraan==1) {
		  $coef[0]+= - ($teken * $factor);
		  $factoraan=0; $factor=1; $teken=1;
	       }
	       $teken*=+1; $factor=1; $tekenaan=1;
	    }
	    elsif ($af eq "-") {
	       if ($factoraan==1) {
		  $coef[0]+= - ($teken * $factor);
		  $factoraan=0; $factor=1; $teken=1;
	       }
	       $teken*=-1; $factor=1; $tekenaan=1;
	    }
	    elsif ($af =~ /^[0-9]/) {
	       $factor = $af;
	       $factoraan=1;
	    }
	    elsif ($af =~ /^[A-Za-z]/) {
	       if ($bekend{$af} eq "") {
		  $avar++;
		  $bekend{$af} = $avar;
		  print "$avar $af\n";
	       }
	       $coef[$bekend{$af}]+= $teken * $factor;
	       $tekenaan=0; $factoraan=0; $factor=1; $teken=1;
	    }
	    elsif ($af eq "\," || $af eq "\;" || $af eq "\>" ){
	       if ($factoraan==1) {
		  $coef[0]+= - ($teken * $factor);
		  $tekenaan=0; $factoraan=0; $factor=1; $teken=1;
	       }
	       if ($tekenaan) { die "Line $regelnr incorrect\n";}
	       for ($aa=0;$aa<=$avar;$aa++) {
		  if ($coef[$aa] != 0 && $column >= $row ) {
		     $matstring.= "$aa $block $row $column $coef[$aa]\n";
		     $aregels++;
		#     if ($aregels%10000==0) { print "$aregels\n";}
		  }
	       }
	       undef @coef;
	       if ($af eq "\,") {
		  $column++;
	       }
	       if ($af eq "\;" || $af =~ /^[\>]/ ) {
		  if ($blocksize ne "") {
		     if ($column > $blocksize) {
			die "Error Line $regelnr: Too many columns\n";
		     }
		     if ($column < $blocksize) {
			die "Error Line $regelnr: Too few columns\n";
		     }
		  }
		  $blocksize=$column;
		  $column=1;
	       }
	       if ($af eq "\;" ) {
		  $row++;
	       }
	       if ($af =~ /^[\>]/) {
		  if ($row > $blocksize) {
		     die "Error Line $regelnr: Too many rows row=$row bl=$blocksize\n";
		  }
		  if ($row < $blocksize) {
		     die "Error Line $regelnr: Too few rows\n";
		  }
		  if ($blocksizestring ne "") {
		     $blocksizestring .= " ";
		  }
		  $blocksizestring .= "$blocksize";
		  $row=1; $column=1; $block++; $blocksize="";
		  $relevant=0;
	       }
	    }
	    else {
	       if ($relevant) {
		  die "Error Line $regelnr: $regel af=$af string=$string\n";
	       }
	    }
	 }
      }
   }
}}
for ($aa=1;$aa<=$avar;$aa++){
   if ( $optstring ne "") {
      $optstring .= " ";
   }
   if ($optcoef[$aa] != 0) {
      $optstring .= "$optcoef[$aa]";
   }
   else {
      $optstring .= "0";
   }
}
print OUTP "$avar\n";
$block--;
print OUTP "$block\n";
print OUTP "$blocksizestring\n";
print OUTP "$optstring\n";
print OUTP "$matstring";
close OUTP;
# system("more $ARGV[0]\.mat\n");
