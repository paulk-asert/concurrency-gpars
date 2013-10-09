def ant = new AntBuilder()
ant.parallel {
    10.times { echo "Message $it" }
}
