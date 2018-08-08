import sys

import pdb

for line in sys.stdin:
    bits = line.strip().split(",")
    lgth = len(bits) - 1
    print("allStations.put(\"" + bits[-1] + "\",\"" + ",".join(bits[0:lgth]).replace("\"", "") + "\");")
    #pdb.set_trace()
