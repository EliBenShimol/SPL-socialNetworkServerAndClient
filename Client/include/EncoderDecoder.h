//
// Created by spl211 on 07/01/2022.
//

#ifndef UNTITLED_ENCODERDECODER_H
#define UNTITLED_ENCODERDECODER_H

class EncoderDecoder{

public:
    EncoderDecoder();
    void shortToBytes(short num, char* bytesArr);
    short bytesToShort(char* bytesArr);

};

#endif //UNTITLED_ENCODERDECODER_H
