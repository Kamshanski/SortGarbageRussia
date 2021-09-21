package com.company.sequential_parser

import edu.kamshanski.tpuclassschedule.utils.string.sequential_parser.SequentialParser

typealias SearchConsumer3<A> = (value: String, beginIndex: Int, appearanceTime: Int, acceptor: A?) -> Unit
typealias SearchConsumer = (value: String, time: Int) -> Unit


open class SequentialParseException(message: String, cause: Throwable? = null): RuntimeException(message, cause)

class NoDataFoundOnTokenException3(token: SearchToken<*>,
                                   time: Int
) : SequentialParseException("No data was found by search token (\"${token.beginMarker}\" : \"${token.endMarker}\") at time $time with " +
        if (token.limit < 1) "no limit" else "a limit ${token.limit}")

class NoDataFoundOnTokenException(beginMarker: String,
                                  endMarker: String,
                                  limit: Int = SearchToken.UNLIMITED,
                                  time: Int
) : SequentialParseException("No data was found by search token (\"${beginMarker}\" : \"${endMarker}\") at time $time with " +
        if (limit < 1) "no limit" else "a limit $limit"
)

fun String.parse() = SequentialParser(this)
