package wordsplit
class TestUtil {
  public static final Map SAMPLES = [
          "This is a sample"         : ['This', 'is', 'a', 'sample'],
          " Here is another sample " : ['Here', 'is', 'another', 'sample'],
          "JustOneWord"              : ['JustOneWord'],
          "Here is a sesquipedalian string of words"
                                     : ['Here', 'is', 'a', 'sesquipedalian',
                                        'string', 'of', 'words'],
          " "                        : [],
          ""                         : []
  ]
}
