while True:
     a = input(">>>: ")
     a = a.lower()
    if a == "q" or a == "quit" or a == "exit":
        break
    elif a == "test" or a == "t":
        targetArray =list(map(int,input("Enter array numbers separated by commas: ").split(",")))
        targetNum = int(input("Enter target num: "))
        cdict = {}
        findx = -10
        sindx = -10
        for i in range(len(targetArray)):
            num = targetArray[i]
            complement = targetNum - num
            if(cdict.get(complement,-10) == -10):
                #value doesent exist
                cdict[complement] = i;
            else:
                findx = i
                sindx = cdict.get(complement)


        if(findx != -10):
            #succesfull finds
            print("Found indexes: " + findx + " + " + sindx)
            print("Values correspond to: " targetArray[findx] + " + " + targetArray[sindx])
            print("Goal value was: " + targetNum)
        else:
            print("Goal value was not found: " + targetNum)

        print("Searched from array: " + targetArray)