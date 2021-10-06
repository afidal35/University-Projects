# LPC - Local Procedure Call

A local procedure call (LPC) framework intended for system programming project

## Install
Make sure to have build essential tools
```shell
make clean
make
```

## Usage
Launch server
```shell
./build/lpc_server.out
```

Launch client(s)
```shell
./build/lpc_client_good1.out
```
```shell
./build/lpc_client_good2.out
```
```shell
./build/lpc_client_good3.out
```
```shell
./build/lpc_client_good4.out
```
```shell
./build/lpc_client_bad1.out
```
```shell
./build/lpc_client_bad2.out
```

There's a script to test concurrency.  
Go to the `scripts` folder  
```shell
$ ./launch_multiple_clients.sh
```