#include "sentMessages.h"
#include "iostream"
#include "thread"
#include "mutex"
#include "connectionHandler.h"
using namespace std;

SentMessages::SentMessages(std::mutex& _mutex, ConnectionHandler* con, condition_variable &convar): mutex(_mutex), _convar(convar){
    connectionHandler=con;
}

void SentMessages::run() {
    while(connectionHandler->getIsConnected()) {
        std::string answer;
        if (!connectionHandler->getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
        }

        int len = answer.length();
        answer = answer.substr(7, len - 8);
        string print="";
        if(answer[0]=='1'){
            if(answer[1]=='0') {
                print = "ACK ";
                int index=3;
                if(answer[2]=='1'&&answer[3]=='2') {
                    print = print + answer[2] + answer[3] + " ";
                    index++;
                }
                else
                    print = print + answer[2]+" ";

                while(index<answer.length()-1){
                    if(answer[index]==',')
                        print+=' ';
                    else
                        print+=answer[index];
                    index++;
                }

            }
            else {
                print = "ERROR ";
                print=print+answer[2];
            }
        }
        else{
            print="NOTIFICATION ";
            if(answer[1]=='0')
                print+="PM ";
            else
                print+="Public ";
            int index=2;
            while(answer[index]!='0'){
                print+=answer[index];
                index++;
            }
            print+=' ';
            index++;
            while(index<answer.length()-1) {
                print += answer[index];
                index++;
            }
        }
        std::cout << print <<std::endl;
        if (answer[1] == '0' && answer[2] == '3') {
            connectionHandler->setIsConnected();
            std::cout << "Exiting...\n" << std::endl;
        }
    }

    connectionHandler->close();

}

