package turbowin;

import java.util.Map;
import java.util.HashMap;

public class HttpStatusMapper {

    private static final Map<Integer, String> CODE_TO_TEXT = new HashMap<>();

    static {
        // 1xx: Information
        CODE_TO_TEXT.put(100, "Continue (HTTP code 100)");
        CODE_TO_TEXT.put(101, "Switching Protocols (HTTP code 101)");
        CODE_TO_TEXT.put(103, "Checkpoint (HTTP code 103)");

        // 2xx: Successful
        CODE_TO_TEXT.put(200, "OK");
        CODE_TO_TEXT.put(201, "Created");
        CODE_TO_TEXT.put(202, "Accepted");
        CODE_TO_TEXT.put(203, "Non-Authoritative Information");
        CODE_TO_TEXT.put(204, "No Content");
        CODE_TO_TEXT.put(205, "Reset Content");
        CODE_TO_TEXT.put(206, "Partial Content");

        // 3xx: Redirection
        CODE_TO_TEXT.put(300, "Multiple Choices (HTTP code 300)");
        CODE_TO_TEXT.put(301, "Moved Permanently (HTTP code 301)");
        CODE_TO_TEXT.put(302, "Moved Temporarily (HTTP code 302)");
        CODE_TO_TEXT.put(303, "See Other (HTTP code 303)");
        CODE_TO_TEXT.put(304, "Not Modified (HTTP code 304)");
        CODE_TO_TEXT.put(305, "Use Proxy (HTTP code 305)");
        CODE_TO_TEXT.put(306, "Switch Proxy (HTTP code 306)");
        CODE_TO_TEXT.put(307, "Temporary Redirect (HTTP code 307)");
        CODE_TO_TEXT.put(308, "Resume Incomplete (HTTP code 308)");

        // 4xx: Client Error
        CODE_TO_TEXT.put(400, "Bad Request (HTTP code 400)");
        CODE_TO_TEXT.put(401, "Unauthorized (HTTP code 401)");
        CODE_TO_TEXT.put(402, "Payment Required (HTTP code 402)");
        CODE_TO_TEXT.put(403, "Forbidden (HTTP code 403)");
        CODE_TO_TEXT.put(404, "Not Found (upload URL unknown) (HTTP code 404)");
        CODE_TO_TEXT.put(405, "Method Not Allowed (HTTP code 405)");
        CODE_TO_TEXT.put(406, "Not Acceptable (HTTP code 406)");
        CODE_TO_TEXT.put(407, "Proxy Authentication Required (HTTP code 407)");
        CODE_TO_TEXT.put(408, "Request Time-out (HTTP code 408)");
        CODE_TO_TEXT.put(409, "Conflict (HTTP code 409)");
        CODE_TO_TEXT.put(410, "Gone (HTTP code 410)");
        CODE_TO_TEXT.put(411, "Length Required (HTTP code 411)");
        CODE_TO_TEXT.put(412, "Precondition Failed (HTTP code 412)");
        CODE_TO_TEXT.put(413, "Request Entity Too Large (HTTP code 413)");
        CODE_TO_TEXT.put(414, "Request-URI Too Large (HTTP code 414)");
        CODE_TO_TEXT.put(415, "Unsupported Media Type (HTTP code 415)");
        CODE_TO_TEXT.put(416, "Requested Range Not Satisfiable (HTTP code 416)");
        CODE_TO_TEXT.put(417, "Expectation Failed (HTTP code 417)");

        // 5xx: Server Error
        CODE_TO_TEXT.put(500, "Internal Server Error (HTTP code 500)");
        CODE_TO_TEXT.put(501, "Not Implemented (HTTP code 501)");
        CODE_TO_TEXT.put(502, "Bad Gateway (HTTP code 502)");
        CODE_TO_TEXT.put(503, "Service Unavailable (HTTP code 503)");
        CODE_TO_TEXT.put(504, "Gateway Time-out (HTTP code 504)");
        CODE_TO_TEXT.put(505, "HTTP Version not supported (HTTP code 505)");
        CODE_TO_TEXT.put(511, "Authentication Required (HTTP code 511)");

        // 7xx: TurboWin+/server Error (custom/self-defined errors) [2 sections]
        //// start first self defined section (must be coordinated with server [index_webstart_101.php]) //////
        CODE_TO_TEXT.put(700, "obs invalid format");
        CODE_TO_TEXT.put(701, "station ID or call sign in the obs not on the email whitelist of this server.<br>"
                + "Please send an email with your station ID and call sign to the addressee National Meteorological Service");
        CODE_TO_TEXT.put(702, "obs routing from server to Meteorological Centre failed");
        //// end first self defined section //////

        //// start second self defined section (no coordination with server [index_webstart_101.php or index_webstart_fm13.php]) //////
        CODE_TO_TEXT.put(710, "internal error when generating format 101 obs");
        //CODE_TO_TEXT.put(711, "most probably no internet connection available or firewall/scanner is blocking");  // actually IOexception
        CODE_TO_TEXT.put(711, "communication error");  // actually IOexception
        CODE_TO_TEXT.put(712, "internal error, malformed URL");
        CODE_TO_TEXT.put(713, "most probably no internet connection available (format 101 obs ok)");
        CODE_TO_TEXT.put(714, "InterruptedException or ExecutionException");
        CODE_TO_TEXT.put(715, "Unsupported UTF-8 encoding");
        CODE_TO_TEXT.put(716, "most probably no internet connection available (FM13 obs ok)");
        CODE_TO_TEXT.put(717, "internal error when generating FM13 obs");
        CODE_TO_TEXT.put(718, "internal error when determining response code");
        //// end second self defined section //////
    }

    public static String httpResponseCodeToText(int responseCode) {
        return CODE_TO_TEXT.getOrDefault(responseCode, "Unknown error");
    }
}
