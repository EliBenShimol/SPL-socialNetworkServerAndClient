//
// Created by spl211 on 08/01/2022.
//

#ifndef UNTITLED_READKEYBOARD_H
#define UNTITLED_READKEYBOARD_H
#include "mutex"
#include "connectionHandler.h"
#include "condition_variable"
using namespace std;
class ReadKeyBoard{

private:
    ConnectionHandler* connectionHandler;
    std::mutex& mutex;
    condition_variable &_convar;


public:
    ReadKeyBoard(std::mutex& _mutex, ConnectionHandler* con, condition_variable &convar);
    void run();
    string getCommend(string commend, string line);
};


#endif //UNTITLED_READKEYBOARD_H
