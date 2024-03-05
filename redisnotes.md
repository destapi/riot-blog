Start Redis instance and exec into it
```
docker run -p 6379:6379 -d --name local-redis --restart always redis/redis-stack-server:latest

docker exec -it local-redis redis-cli
```

Or alternatively, just use on command

```
docker run -p 6379:6379 -it redis/redis-stack-server:latest redis-cli
```
