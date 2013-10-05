// adapted from the GPars sample file DemoForkJoinFileCounter.groovy
//@Grab('org.codehaus.gpars:gpars:0.10')
import static groovyx.gpars.GParsPool.withPool
import static groovyx.gpars.GParsPool.runForkJoin

withPool {
    def (sum, totalSize) = countAndSize('.')
    println "Number of files: $sum ($totalSize bytes)"
}

def countAndSize(dir) {
    runForkJoin(new File(dir)) {currentDir ->
        long count = 0
        long size = 0
        currentDir.eachFile { file ->
            if (file.isFile()) {
                count++
                size += file.size()
            }
            else if (file.name != '.svn') {
                println "Forking a thread for $file"
                forkOffChild file
            }
        }
        println childrenResults.dump()
        [count + childrenResults.sum(0){ it[0] },
          size + childrenResults.sum(0){ it[1] }]
    }
}
