import org.gruple.SpaceService

def defaultSpace = SpaceService.getSpace()
defaultSpace << [fname:"Vanessa", lname:"Williams", project:"Gruple"]
println defaultSpace.get(fname:"Vanessa", lname:"Williams", project:"Gruple")
