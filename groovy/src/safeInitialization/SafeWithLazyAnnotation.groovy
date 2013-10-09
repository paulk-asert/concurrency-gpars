class Main {
    @Lazy Resource first
    @Lazy volatile Resource second
    @Lazy volatile Resource third = { new Resource(/* args */) }()
    @Lazy(soft = true) volatile Resource fourth
}
