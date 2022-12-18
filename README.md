# Project_Challenge

## Description

Familiarize yourself with parallel partial reduction 
[trees](https://www.sciencedirect.com/topics/computer-science/partial-reduction).

In this exercise, your task is to scan left on a given input array 
and output a new array consisting of the maximum values 
to the left of a given index in the original array.

Example:
```
In: [0, 0, 1, 5, 2, 3, 6]

Out: [0, 0, 1, 5, 5, 5, 6]
```

### Task 1
Using the partial parallel reduction tree paradigm, implement the methods described in [ScanInterface](./src/main/scala/com/intellias/challenge/ScanInterface.scala), 
making use of the parallelism abstractions provided. You should not utilize
data or function parallel constructs that have not been provided.

Utilize good judgment when choosing side-effecting vs pure implementations to
blend good functional style with performance.

Note that a [trivial sequential
implementation](./src/main/scala/com/intellias/challenge/SequentialScan.scala) has been provided for reference.

### Task 2
Using scalameter, compare performance between a fully sequential implementation
and your parallel implementation, and provide some recommendations for optimal
threshold values for your system.

I've provided sample scalameter reports that were generated using `ParallelScanPerfSpec` in [performance directory](performance/reports).
Tests were performed on machine with following configuration:
```
cores: 8
name: OpenJDK 64-Bit Server VM
osArch: aarch64
osName: Mac OS X
vendor: Azul Systems, Inc.
version: 11.0.12+7-LTS
```
used JVM options: `-server -Xmx2048m -Xms2048m`

Because of time constraint generated input arrays were growing range - for current implementation it should represent worst case where each element is bigger than previous.

Correctness of solution is validated by dedicated UTs that can be found in `com.intellias.challenge.ParallelScanSpec`.

Few main conclusions mainly based on [this](performance/reports/parallel_in_different_configs_vs_seq_up_to_50kk%20elements) report:
- SequentialScan 
  - no surprises, execution time is proportional to input array size
- ParallelScan
  - for measured configurations of input size and threshold we see noticeable gains resulting from the parallelization of 
    calculations around `1_000_000` elements in input array, for the most of the tested thresholds
  - `threshold` value shouldn't be too small, but also shouldn't be too close to input array size 
    - too low `threshold` value can affect negatively execution time - parallelization has its own cost related to context switching and efficiency of CPU caches usage
    - too big `threshold` value will cause that we don't have so much parallelization, but we need to pay a price related to creation temporarily `Tree` representation of input
    - the best results were observed for `threshold = 31_250` which was surprising for me,
      I was thinking that because processing nature (CPU intensive, without I/O/async operations) the best performant threshold will be sth around `input size / number of cores`.
  - in case of specialised implementation, not generic that could take any reduction function we could apply some optimisations that are related to applied reduction function, I've described one as `TODO` comment in `com.intellias.challenge.ParallelScan` 
  


### Task 3
Using your observations from Task 2, extrapolate to general systems.

Parallelization of CPU intensive tasks can reduce execution time, but it should be applied carefully and only if we know whole context of given process where it will be used:
- such implementation is almost always more complicated -> harder to maintain and analyse in case of issues
- it is not for free, we reduce execution time, but it will be always more compute power consuming (frequently also memory as we need create temp structures, additional threads also bring some overhead) 
- we need to have big enough input datasets, otherwise benefits will not outweigh the costs
  - in case of mixed inputs we can have hybrid solution that use parallelization only if some conditions are met
- in case of real life problems, except big data where we have already predefined tools and solutions, CPU intensive tasks are not bottlenecks,
  or even if they are, it is more a symptom of a problem few layers higher, like mistakes during process modeling or wrong abstraction e.g.
  we need handle huge datasets because no one thought to shard/partition them per user/entity as 99% of functionalities we have acts on per user/entity data
- regular load/soak/performance tests allows you to know where your system bottleneck are and give you particular data that allows you to track regression of system/give you hints what should be addressed with the highest priority


## FAQ
1. Current challenge uses Scala 13 
2. Please, do not forget about DoD for development tasks
3. For Task 2, please use the randomized numbers array, not sorted before. 

