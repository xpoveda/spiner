
#echo "*************************************"

ficheroreg=./tmp/fichero.reg
salidareg=./tmp/salida.reg
salida=/mnt/hgfs/CompartidaUbuntu/salida.txt

numlineas=1
cat /dev/null > $salidareg
cat /dev/null > $salida

while read linea
do
  if [ $numlineas -eq 1 ]
  then
      cabecera=`echo $linea`
  else      
      registro=`echo $linea`  
      #echo "Tratando registro[" $registro "]"
      echo $cabecera >  $ficheroreg
      echo $registro >> $ficheroreg
      java -classpath . spiner $2 $3 $ficheroreg $4 > $salidareg   
      cat $salidareg >> $salida
        
      if [ "$5" == "simple" ]
      then
      	cat $salidareg
	exit
      fi

      if [ "$5" == "todolista" ]
      then	
        cat $salidareg
      fi
  fi
  
  numlineas=`expr $numlineas + 1`    
  
done < $1

sed "s/\#apos/\\\'/g" $salida > $salida.tmp
iconv --from-code=utf-8 --to-code=iso-8859-1//TRANSLIT $salida.tmp -o $salida.tmp2
mv $salida.tmp2 $salida
rm $salida.tmp
