#include <cstdlib>
#include <connectionHandler.h>
#include <EncoderDecoder.h>
#include "readKeyBoard.h"
#include "sentMessages.h"
#include "thread"
#include <condition_variable>
#include <deque>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(1, host, port);
    ConnectionHandler* con=connectionHandler.getInstance();
    if (!con->connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::mutex mutex;
    condition_variable convar;
    ReadKeyBoard r(mutex, con, convar);
    SentMessages s(mutex, con, convar);
    std::thread t1(&ReadKeyBoard::run, &r);
    std::thread t2(&SentMessages::run, &s);
    t1.join();
    t2.join();
    return 0;


}
