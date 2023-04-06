#include "iostream"
#include "string"
#include "stdio.h"
#include "time.h"
#include "readKeyBoard.h"
#include "boost/bind.hpp"
#include "mutex"
#include "condition_variable"
#include "connectionHandler.h"
#include "future"
#include "chrono"

using namespace std;

ReadKeyBoard::ReadKeyBoard(std::mutex& _mutex, ConnectionHandler* con, condition_variable &convar): mutex(_mutex), _convar(convar){
    connectionHandler=con;
}


void ReadKeyBoard::run() {
    while (connectionHandler->getIsConnected()) {

            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            string command = "";
            int index = 0;
            while (index < line.length() && line[index] != ' ') {
                command += line[index];
                index++;
            }
            line = line + '0';
            string newLine = "";
            index++;
            while (index < line.length()) {
                newLine += line[index];
                index++;
            }

            string ret = getCommend(command, newLine);
            int len = ret.length();
            if (!connectionHandler->sendLine(ret)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
            } else
            if (ret[0] == '3')
                sleep(1);

    }
    connectionHandler->close();
}


string ReadKeyBoard::getCommend(string commend, string line) {
    if(commend=="REGISTER"||commend=="LOGIN"){
        int count=0;
        int places[]={0, 0, 0};
        for(int i=0;i<line.length();i++){
            if(line[i]==' ') {
                if(count<2) {
                    places[count] = i;
                    line[i]='0';
                    count++;
                }
                else{
                    line[places[0]]=' ';
                    line[i]='0';
                    places[0]=places[1];
                    places[1]=places[2];
                    places[2]=i;
                }
            }
        }
        if(commend=="REGISTER")
            line='1'+line;
        else
            line='2'+line;
        return line;
    }


    else if(commend=="LOGOUT"){
        line="3";
        return line;
    }


    else if(commend=="FOLLOW"||commend=="POST"||commend=="BLOCK"){
        if(commend=="FOLLOW"){
            string newLine="";
            for(int i=0;i<line.length();i++) {
                if (line[i] != ' ')
                    newLine += line[i];
            }
            line='4'+newLine;
        }
        else if(commend=="POST")
            line='5'+line;
        else
            line="12"+line;
        return line;
    }


    else if(commend=="PM"){
        int index=0;
        while(line[index]!=' ')
            index++;
        line[index]='0';
        time_t t = time(NULL);
        struct tm  tm=*localtime(&t);
        char       buf[80];
        sprintf(buf, "%d-%d-%d %d:%d", tm.tm_year+1900, tm.tm_mon+1, tm.tm_mday, tm.tm_hour, tm.tm_min);
        line=line+buf+'0';
        line='6'+line;
        return line;
    }
    else if(commend=="LOGSTAT"){
        line="7";
        return line;
    }

    else if(commend=="STAT"){
        for(int i=0;i<line.length();i++){
            if(line[i]==' ')
                line[i]='|';
        }
        line="8"+line;
        return line;
    }


    return "13";
}
