<div style="display: flex; gap: 10px;">

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ljw1126_helloInventory&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ljw1126_helloInventory)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ljw1126_helloInventory&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ljw1126_helloInventory)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ljw1126_helloInventory&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ljw1126_helloInventory)

</div>

# act

### act 다운로드
https://nektosact.com/installation/index.html
https://nektosact.com/
https://github.com/nektos/act

release 다운로드 후 압축 해제(생략)
```shell
$ sudo mv act /usr/local/bim
$ act --version
act version 0.2.69
```

### act 실행

```shell
act -P ubuntu-22.04=catthehacker/ubuntu:act-22.04 {pull_request | push}
```



---
# reference.
**github action**
- https://docs.github.com/ko/actions
- https://docs.github.com/ko/actions/use-cases-and-examples/building-and-testing/building-and-testing-java-with-gradle

**Jacoco**
- https://www.jacoco.org/jacoco/
