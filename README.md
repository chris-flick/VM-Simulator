# VM-Simulator
Implementation of the Optimal, Clock, NRU, and Aging algorithms for page replacement

vmsim.java is the source file. Compile and run with the following command line arguments "java vmsim -n <numframes> -a <opt|clock|nru|aging> [-r <refresh rate>] <tracefile>"

In place of numframes, enter a number.

After the '-a', choose one of the algorithms within <>.

The -r is optional, if so, include '-r' followed by a number depicting the refresh rate.

The tracefiles are included and are gcc.trace and bzip.trace.
