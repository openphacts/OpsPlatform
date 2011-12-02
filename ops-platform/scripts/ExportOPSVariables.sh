# This script exports the variables used in the
# RunLarKC.sh script and requries to be called from the
# script directory.

if [[ `pwd` != */openphacts/ops-platform/scripts* ]]
then
	echo Error. 
	echo Call this script form the openphacts/ops-platform/scripts directory. 
else 
	alias realpath=''
	OPSPWD=`pwd`
	export MAVEN_OPTS=-Xmx512m
	export OPS_PATH=`readlink -f "$OPSPWD"/../../..`   # - path where OPS repository is checked out
	export LARKC_PATH=$OPS_PATH           # path where LarKC is checked out

	echo MAVEN_OPTS: $MAVEN_OPTS
	echo OPS_PATH: $OPS_PATH
	echo LARKC_PATH: $LARKC_PATH
fi
