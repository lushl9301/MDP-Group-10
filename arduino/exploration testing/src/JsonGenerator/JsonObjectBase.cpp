/*
* Arduino JSON library
* Benoit Blanchon 2014 - MIT License
*/

#include "JsonObjectBase.h"
#include <string.h> // for strcmp

using namespace ArduinoJson::Generator;
using namespace ArduinoJson::Internals;

JsonValue JsonObjectBase::nullValue;

size_t JsonObjectBase::printTo(Print& p) const
{
    size_t n = 0;

    n += p.write('{');

    // NB: the code has been optimized for a small size on a 8-bit AVR

    const KeyValuePair* current = items;
    for (int i = count; i > 0; i--)
    {       
        n += EscapedString::printTo(current->key, p);
        n += p.write(':');
        n += current->value.printTo(p);

        current++;

        if (i > 1)
        {
            n += p.write(',');
        }
    }

    n += p.write('}');

    return n;
}

JsonObjectBase::KeyValuePair* JsonObjectBase::getMatchingPair(JsonKey key) const
{
    KeyValuePair* p = items;

    for (int i = count; i > 0; --i)
    {
        if (!strcmp(p->key, key))
            return p;

        p++;
    }

    return 0;
}

JsonValue& JsonObjectBase::operator[](JsonKey key)
{
    KeyValuePair* match = getMatchingPair(key);

    if (match)
        return match->value;

    JsonValue* value;

    if (count < capacity)
    {
        items[count].key = key;
        value = &items[count].value;
        count++;
    }
    else
    {
        value = &nullValue;
    }

    value->reset();
    return *value;
}

bool JsonObjectBase::containsKey(JsonKey key) const
{
    return getMatchingPair(key) != 0;
}

void JsonObjectBase::remove(JsonKey key)
{
    KeyValuePair* match = getMatchingPair(key);    
    if (match == 0) return;

    *match = items[--count];
}
