//@Grab('org.codehaus.gpars:gpars:0.10')
import static groovyx.gpars.GParsPool.withPool
import static groovyx.gpars.GParsPool.runForkJoin

withPool {
    def (sum, totalSize, max) = stats('..')
    println """
    Number of files: $sum ($totalSize bytes)
    Biggest file: ${max[0]} (${max[1]} bytes)
    """.stripIndent()
}

def stats(dir) {
    runForkJoin(new File(dir)) {currentDir ->
        long count = 0
        long size = 0
        long max = 0
        String maxName = 'unknown'
        currentDir.eachFile { file ->
            if (file.isFile()) {
                count++
                size += file.size()
                if (file.size() > max) {
                    max = file.size()
                    maxName = file.name
                }
            }
            else if (file.name != '.svn') {
                println "Forking a thread for $file"
                forkOffChild file
            }
        }
        [count + childrenResults.sum(0){ it[0] },
          size + childrenResults.sum(0){ it[1] },
          (childrenResults.collect{ it[2] } + [[maxName, max]]).max{ it[1] }]
    }
}
