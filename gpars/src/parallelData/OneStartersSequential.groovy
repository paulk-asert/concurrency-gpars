def oneStarters = (1..30)
    .collect{ it ** 2 }
    .findAll{ it ==~ '1.*' }
assert oneStarters == [1, 16, 100, 121, 144, 169, 196]
