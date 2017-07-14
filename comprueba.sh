
echo "****"
echo "En ultraedit siempre convertir a ascii a utf edicion unicode"
echo "****"

datos=/mnt/hgfs/CompartidaUbuntu/datos.txt
patron=/mnt/hgfs/CompartidaUbuntu/patron.txt
sinonimos=/mnt/hgfs/CompartidaUbuntu/sinonimos.txt
salida=/mnt/hgfs/CompartidaUbuntu/salida.txt

ls -lt   $datos
ls -lt   $patron
ls -lt   $sinonimos
ls -lt   $salida

echo "****"

file -bi $datos
file -bi $patron
file -bi $sinonimos
file -bi $salida

echo "****"
