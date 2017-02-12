docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker network create -d bridge at-network
echo "Created Network bridge"
docker build -f Dockerfile -t rmi-app .
echo "Docker image created! Now starting server and client..."
docker run -d --net=at-network --name pingserver rmi-app sh -c "cd /code; make clean;make all-classes; javac PingPong/*.java; java PingPong.PingPongServerFactory" 
sleep 10
docker run -d --net=at-network --name pingclient rmi-app sh -c "cd /code; make clean;make all-classes; javac PingPong/*.java; java PingPong.PingPongClient"
echo "Server and client up!"
docker logs -f pingclient

