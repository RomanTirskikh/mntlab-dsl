def gitURL = "https://github.com/MNT-Lab/mntlab-dsl.git"
def command = "git ls-remote -h $gitURL"
def proc = command.execute()
proc.waitFor()
if ( proc.exitValue() != 0 ) { 
  println "Error, ${proc.err.text}" 
  System.exit(-1)
}

def repobr = proc.in.text.readLines().collect { 
  it.replaceAll(/[a-z0-9]*\trefs\/heads\//, '')
}


job("EPBYMINW3093/MNTLAB-asemirski-main-build-job") {
		scm {
       			github 'MNT-Lab/mntlab-dsl', '$BRANCH_NAME'
 		 }
	       	 parameters {
   			 choiceParam('BRANCH_NAME', ['asemirski', 'master'], 'Choose branch')
			 activeChoiceParam('CHOICE-1') {
				description('Allows user choose from multiple choices')
				filterable()
				choiceType('SINGLE_SELECT')
				groovyScript {
                			script('["choice1", "choice2"]')}
			 
  		 }
		for (i = 1; i <2; i++) {
 			 job("EPBYMINW3093/MNTLAB-asemirski-child${i}-build-job") {
				scm {
       					github 'MNT-Lab/mntlab-dsl', '$BRANCH_NAME'
    				}

				parameters {choiceParam("BRANCH_NAME", repobr,'Choose branch')}	
				steps {
        				shell('chmod +x ./script.sh; ./script.sh > output.txt; tar -czf ${BRANCH_NAME}_dsl_script.tar.gz script.sh')
       				}
				publishers {
       			 		archiveArtifacts {
                       				pattern('${BRANCH_NAME}_dsl_script.tar.gz')
           					pattern('output.txt')
                       					 }
    					   }
				}
		}
}
}
