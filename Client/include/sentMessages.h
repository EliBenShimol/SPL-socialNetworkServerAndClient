//
// Created by spl211 on 08/01/2022.
//

#ifndef UNTITLED_SENTMESSAGES_H
#define UNTITLED_SENTMESSAGES_H

#include "connectionHandler.h"
#include "mutex"
#include "condition_variable"
class SentMessages{

private:
    ConnectionHandler* connectionHandler;
    std::mutex& mutex;
    std::condition_variable &_convar;


public:
    SentMessages(std::mutex& _mutex, ConnectionHandler* con, std::condition_variable &convar);
    void run();
};



#endif //UNTITLED_SENTMESSAGES_H
