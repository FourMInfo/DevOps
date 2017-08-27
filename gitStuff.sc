import scala.util.Try
import scala.util.{Success,Failure}

// you need to have a checked out version of the Common project in a parallel directory
// https://github.com/FourMInfo/DevOps.git
import $file.^.Common.logs
import logs._

@doc("Initial creation, commit & connect upstream of local repository to remote repository which was created with License or ReadMe")
@main
def git2Github(repository:String @doc("URL of remote repository")) = {
    // The process used here is based on the following links:
    // https://help.github.com/articles/adding-an-existing-project-to-github-using-the-command-line/
    // https://stackoverflow.com/questions/37937984/git-refusing-to-merge-unrelated-histories
    // https://gist.github.com/heiswayi/350e2afda8cece810c0f6116dadbe651
    // initialize log
    val logFile = makeLog
    // initialize local repository
    val gitInitT = Try(%%('git,"init"))
    val continueOn = contOrNot(gitInitT," initialize local git ", logFile)
    // add local files
    val continueOn1 = 
        if (continueOn) {
            val gitAddT = Try(%%('git, "add", "."))
            contOrNot(gitAddT," add files to git ", logFile)
        } else {
            false
        }
    // commit local files
    val continueOn2 = 
        if (continueOn1) {
            val gitCommitT = Try(%%('git, "commit", "-m", "Initial commit"))
            contOrNot(gitCommitT," commit files to git ", logFile)
        } else {
            false
        }
    // connect local to remote repository
    val continueOn3 = 
        if (continueOn2) {
            val gitRemoteT = Try(%%('git, "remote", "add", "origin", repository))
            contOrNot(gitRemoteT,s" connect local to remote repository $repository ", logFile)
        } else {
            false
        }
    // check remote & display 
    val continueOn4 = 
        if (continueOn3) {
            val gitRemoteCT = Try(%%('git, "remote", "-v"))
            contOrNot(gitRemoteCT," check & display remote repository ", logFile)
        } else {
            false
        }
    // pull files from remote even though history is currently unrelated  
    val continueOn5 = 
        if (continueOn4) {
            val gitRemoteCT = Try(%%('git, "pull", "origin", "master", "--allow-unrelated-histories"))
            contOrNot(gitRemoteCT," pull files from remote repository ", logFile)
        } else {
            false
        }
    // push local files to remote and set remote master as upstream
    val continueOn6 = 
        if (continueOn5) {
            val gitRemoteUCT = Try(%%('git, "push", "--set-upstream", "origin", "master"))
            contOrNot(gitRemoteUCT," push files to remote repository ", logFile)
        } else {
            false
        }
}

