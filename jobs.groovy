def studname = "rtirskikh"
def jobsList = []
def gitcommand = "git ls-remote -h -t https://github.com/RomanTirskikh/mntlab-dsl.git"
println "step1"
job("EPMFARMDVO300-MNTLAB-${studname}-main-build-job") {
		def selectedBranches = gitcommand.execute().text.readLines().collect {it.split()[1].replaceAll('refs/heads/', '')
	}
		selectedBranches.removeAll 
			{
				!(["master",studname].contains(it)) 
			}
println "step2"		
for (i = 1; i <= 4; i++)
	{ 
		parameters 
			{
				println "step3"
				choiceParam('BRANCH_NAME',selectedBranches,'')
				booleanParam("EPMFARMDVO300-MNTLAB-${studname}-child${i}-build-job", true,"")
			}

    //create downstream job
	println "step4"
    job("EPMFARMDVO300-MNTLAB-${studname}-child${i}-build-job") 
		{
			scm 
				{
					github('RomanTirskikh/mntlab-dsl', studname)
				}
			def allBranches = gitcommand.execute().text.readLines().collect {it.split()[1].replaceAll('refs/heads/', '')
		}
	println "step5"
	allBranches.remove(studname)
	allBranches.add(0,studname)
	parameters 
		{
			choiceParam('BRANCH_NAME',  allBranches,'')
		}
	steps 
		{
			shell('chmod 777 ./script.sh; ./script.sh > output.txt')
			shell('tar -czf ${BRANCH_NAME}_dsl_script.tar.gz script.sh jobs.groovy' )
			shell('cat output.txt' )
		}
		publishers 
		{
			archiveArtifacts '${BRANCH_NAME}_dsl_script.tar.gz, output.txt'
		}
	}	
	println "step6"
    jobsList << "MNTLAB-${studname}-child${i}-build-job"
    steps 
		{
			downstreamParameterized 
				{
					println "step7"
					trigger("EPMFARMDVO300-MNTLAB-${studname}-child${i}-build-job") 
						{
							block
								{
									println "step8"
									buildStepFailure('FAILURE')
									failure('FAILURE')
									unstable('UNSTABLE')
								}
							parameters 
								{
									println "step9"
									predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
								}
						}
				}    
		}
	}
}