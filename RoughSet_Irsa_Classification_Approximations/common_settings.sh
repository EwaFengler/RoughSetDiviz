# Adapt the two following lines -- NB: Java 8 is required
JAVA_HOME=$JAVA_HOME
XMCDA_LIB="../xmcda/XMCDA-java.jar"


# -- You normally do not need to change anything beyond this point --

JAVA=${JAVA_HOME}/bin/java

# check that the XMCDA library can be found and that Java is executable
if [ ! -f ${XMCDA_LIB} -o "" == "${JAVA_HOME}" ]; then
  echo "Please modify common_settings.sh to reflect your installation (see README)" >&2
  exit -1;
fi
if [ ! -x "${JAVA}" ]; then
  echo "Java: ${JAVA}: not found -- please edit common_settings.sh and check JAVA_HOME"
  exit -1;
fi

CLASSPATH="./bin:${XMCDA_LIB}:${CLASSPATH}"
CMD="${JAVA} -cp ${CLASSPATH} pl.poznan.put.xmcda.RoughSet_Irsa_Classification_Approximations"
echo $CMD
export JAVA_HOME
