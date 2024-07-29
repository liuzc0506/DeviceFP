import re

res = []
f = open("sysProp.txt","r")
lines = f.readlines()
for line in lines:
    # prop = re.findall(r"[[](.*?)[]]",line)
    prop = line[line.find("[")+1:line.find("]")]
    # print(prop)
    res.append('"'+prop+'"')
f.close()

done =[]
f = open("sysProp_done.txt","r")
for linr in f.readlines():
    done.append(linr.strip())
f.close()
# print(done)
f = open("sysProp_done.txt","a")
for it in res:
    if it in done:
        continue
    f.write(it + ',\n')
f.close()