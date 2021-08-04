import os
import sys
import datetime

# Import smtplib for the actual sending function
import smtplib, ssl

# Import the email modules we'll need
# The central class in the email package is the EmailMessage class, imported from the email.message module
from email.message import EmailMessage 


# printing general info
print ("[date-time logging: ", datetime.datetime.utcnow().strftime("%d %b %Y %H:%M:%S %Z"),"UTC", "]")
print ("TurboWin+ python email module (build 3-February-2021 13:00 UTC)")
print ("Script name: ", sys.argv[0])
print ("Number of arguments: ", len(sys.argv))
#print ("The arguments are: " , str(sys.argv))

#verify number of arguments
required_num_arguments = 11
if (len(sys.argv) != required_num_arguments):
    print ("invalid number of arguments (must be", required_num_arguments,")")
    sys.exit(1)

#read arguments
# NB module name (file name) = sys.argv[0]
smtp_mode             = sys.argv[1]       # eg SMTP_HOST_SHIP, GMAIL_TLS, GMAIL_SSL
smtp_host_local       = sys.argv[2]       # eg 'smtp.gmail.com' or 'smtp.knmi.nl'
smtp_password_local   = sys.argv[3]       # eg 'P@ssword!' or 'null'
send_to               = sys.argv[4]       # 'martin.stam@home.nl'
send_from             = sys.argv[5]       # 'turbowin.observations@gmail.com'
subject               = sys.argv[6]       #'dit is een test via ' + smtp_mode
body                  = sys.argv[7]       # "This is an obs test body line"
send_cc               = sys.argv[8]       # "çc email address"
smtp_port_local       = sys.argv[9]       # only used in case of SMTP_HOST_SHIP and CUSTOM (eg 'null'in case of Gmail)
attachment            = sys.argv[10]      # 'none' / 'yes'

print ("The arguments are:")
print("smtp_mode           = ", smtp_mode)
print("smtp_host_local     = ", smtp_host_local)
print("smtp_password_local = ", "******")  #print("smtp_password_local = ", smtp_password_local)
print("send_to             = ", send_to)
print("send_from           = ", send_from)
print("subject             = ", subject)
print("body                = ", body)
print("send_cc             = ", send_cc)
print("smtp_port_local     = ", smtp_port_local)
print("attachment          = ", attachment)
print("")

msg = EmailMessage()
msg['Subject']        = subject
msg['From']           = send_from
msg['To']             = send_to

#add cc only if cc not empty 
if send_cc != "null":
    msg['Cc']         = send_cc    

msg.set_content(body)


# add attachment if required 
if (attachment == "yes"):
    try:
        dirname = os.path.dirname(__file__)                                          # present dir
        file = os.path.join(dirname, "../format_101/temp/HPK_format_101.txt") # up 1 dir and then further
        
        # hieronder test
        #file = "C:/NetBeansProjects/turbowin_jws/dist/jlink/turbowin_jws/bin/logs/format_101/temp/HPK_format_101.txt"
        # test
        
        # NB see: https://docs.python.org/3/library/email.examples.html
        with open(file, 'rb') as fp:
        #    data = fp.read()
            
            #msg.add_attachment(fp.read(), maintype=maintype, subtype=subtype, filename=filename)
            msg.add_attachment(fp.read(), maintype='application', subtype='octet-stream', filename=('iso-8859-1', '', 'HPK_format_101.txt')) 
            
                
        #msg.add_attachment(data, maintype='application', subtype='octet-stream', filename=('iso-8859-1', '', 'HPK_format_101.txt'))
        #msg.add_header('Content-Disposition', 'attachment', filename=('iso-8859-1', '', 'HPK_format_101.txt'))  #set filename

    except:
        print("Unable to open attachment ../format_101/temp/HPK_format_101.txt") # always ths path
        sys.exit(14)




# -----  TESTING BEGIN  -----
#
#print("TEST TEST TEST")
#smtp_mode      = "CUSTOM_SSL" #"SMTP_HOST_SHIP" # "GMAIL_SSL" #"YAHOO_TLS" #"LOCALHOST_SHIP"  #"YAHOO_SSL" # "YAHOO_TLS"
#print(smtp_mode)
#
# ------ TESTING END ------
 


# Send the message via local SMTP server.
# https://docs.python.org/3/library/smtplib.html
#
# NB For normal use, you should only require the initialization/connect, sendmail(), and SMTP.quit() methods. An example is included below.
#
try:

    # Create a secure SSL context
    context = ssl.create_default_context()


    if (smtp_mode == "GMAIL_TLS") :
      
        smtp_host      = smtp_host_local                   # eg 'smtp.gmail.com'
        smtp_port      = 587                               # moet komen na msg = EmailMessage() !!!!!!!!!????????
        smtp_password  = smtp_password_local               # eg "xxipzrwzxfjhrcls" 
         
        smtp_server = smtplib.SMTP(smtp_host, smtp_port)
        smtp_server.starttls(context=context)              # Secure the connection

        smtp_server.login(send_from, smtp_password)
        smtp_server.send_message(msg)
        smtp_server.quit()
        
    elif (smtp_mode == "GMAIL_SSL"):
        
        smtp_host      = smtp_host_local                    # eg 'smtp.gmail.com'
        smtp_port      = 465                                # moet komen na msg = EmailMessage() !!!!!!!!!????????
        smtp_password  = smtp_password_local                # eg "xxipzrwzxfjhrcls" 

        smtp_server = smtplib.SMTP_SSL(smtp_host, smtp_port)
        smtp_server.login(send_from, smtp_password)
        smtp_server.send_message(msg)
        smtp_server.quit()
    
    elif (smtp_mode == "YAHOO_TLS"):
        
        smtp_host      = smtp_host_local                     # eg 'smtp.mail.yahoo.com'
        smtp_port      = 587                                 # moet komen na msg = EmailMessage() !!!!!!!!!????????
        smtp_password  = smtp_password_local                 # eg "xxthpddamdhqucxz"

        smtp_server = smtplib.SMTP(smtp_host, smtp_port)
        smtp_server.starttls(context=context) # Secure the connection

        smtp_server.login(send_from, smtp_password)
        smtp_server.send_message(msg)
        smtp_server.quit()
 
    elif (smtp_mode == "YAHOO_SSL"):
        
        smtp_host      = smtp_host_local                      # eg 'smtp.mail.yahoo.com'
        smtp_port      = 465                                  # moet komen na msg = EmailMessage() !!!!!!!!!????????
        smtp_password  = smtp_password_local                  # eg "xxthpddamdhqucxz"
        
        smtp_server   = smtplib.SMTP_SSL(smtp_host, smtp_port)
        smtp_server.login(send_from, smtp_password)
        smtp_server.send_message(msg)
        smtp_server.quit()
        
    elif (smtp_mode == "SMTP_HOST_SHIP"):
 
        if (smtp_password_local != "null"):
  
            smtp_host      = smtp_host_local                  # eg 'smtp.ziggo.nl'
            smtp_port      = int(smtp_port_local)             # moet komen na msg = EmailMessage() !!!!!!!!!????????
            #smtp_port      = 587
            smtp_password  = smtp_password_local              # eg "xxipzrwzxfjhrcls"  
         
            smtp_server = smtplib.SMTP(smtp_host, smtp_port)
            smtp_server.starttls(context=context) # Secure the connection

            smtp_server.login(send_from, smtp_password)
            smtp_server.send_message(msg)
            smtp_server.quit()
  
        else:
            # password and port will not be used
            smtp_host      = smtp_host_local                  # eg 'smtp.ziggo.nl' 
            smtp_server = smtplib.SMTP(smtp_host)
            smtp_server.send_message(msg)
            smtp_server.quit()
    
    elif (smtp_mode == "CUSTOM_TLS"): 
        smtp_host      = smtp_host_local                      # eg: 'smtp.mail.yahoo.com'
        smtp_port      = int(smtp_port_local)                 # moet komen na msg = EmailMessage() !!!!!!!!!????????
        smtp_password  = smtp_password_local                  # eg: "xxthpddamdhqucxz"

        smtp_server = smtplib.SMTP(smtp_host, smtp_port)
        smtp_server.login(send_from, smtp_password)
        smtp_server.send_message(msg)
        smtp_server.quit()
    
    elif (smtp_mode == "CUSTOM_SSL"):
        
        smtp_host      = smtp_host_local                      # eg 'smtp.gmail.com'
        smtp_port      = int(smtp_port_local)                 # moet komen na msg = EmailMessage() !!!!!!!!!????????
        smtp_password  = smtp_password_local                  # eg "xxipzrwzxfjhrcls" 

        smtp_server = smtplib.SMTP_SSL(smtp_host, smtp_port)
        smtp_server.login(send_from, smtp_password)
        smtp_server.send_message(msg)
        smtp_server.quit()
       
    elif (smtp_mode == "CUSTOM_TLS_STARTTLS"):      
        smtp_host      = smtp_host_local                      # eg: 'smtp.mail.yahoo.com'
        smtp_port      = int(smtp_port_local)                 # moet komen na msg = EmailMessage() !!!!!!!!!????????
        smtp_password  = smtp_password_local                  # eg: "xxhpddamdhqucxz"

        smtp_server = smtplib.SMTP(smtp_host, smtp_port)
        smtp_server.starttls(context=context)                 # Secure the connection
        smtp_server.login(send_from, smtp_password)
        smtp_server.send_message(msg)
        smtp_server.quit()

    elif (smtp_mode == "CUSTOM_SSL_STARTTLS"):
        smtp_host      = smtp_host_local                      # eg 'smtp.gmail.com'
        smtp_port      = int(smt_port_local)                  # moet komen na msg = EmailMessage() !!!!!!!!!????????
        smtp_password  = smtp_password_local                  # eg "xxipzrwzxfjhrcls" 

        smtp_server = smtplib.SMTP_SSL(smtp_host, smtp_port)
        smtp_server.starttls(context=context)                 # Secure the connection
        smtp_server.login(send_from, smtp_password)
        smtp_server.send_message(msg)
        smtp_server.quit()
    
    else:   
        print ("invalid smtp_mode (",smtp_mode,")")
        sys.exit(2) 


#except (IOError, OSError) as ex:
#    print("error sending email to " + send_to + ".", "Error: " +  str(ex))
#    #self.retry((to_name, to_addr, subject, text, html), countdown=cfg['MAIL_RETRY'])
    
except smtplib.SMTPServerDisconnected:
    print("This exception is raised when the server unexpectedly disconnects, or when an attempt is made to use the SMTP instance before connecting it to a server.")
    sys.exit(3)
except smtplib.SMTPSenderRefused:
    print("Sender address refused. In addition to the attributes set by on all SMTPResponseException exceptions, this sets ‘sender’ to the string that the SMTP server refused.")
    sys.exit(4)
except smtplib.SMTPRecipientsRefused:
    print("All recipient addresses refused. The errors for each recipient are accessible through the attribute recipients, which is a dictionary of exactly the same sort as SMTP.sendmail() returns.")
    sys.exit(5)
except smtplib.SMTPDataError:
    print(" The SMTP server refused to accept the message data.")
    sys.exit(6)
except smtplib.SMTPConnectError:
    print("Error occurred during establishment of a connection with the server.")
    sys.exit(7)
except smtplib.SMTPHeloError:
    print("The server refused our HELO message.")
    sys.exit(8)
except smtplib.SMTPNotSupportedError:
    print("The command or option attempted is not supported by the server.")
    sys.exit(9)
except smtplib.SMTPAuthenticationError:
    print("SMTP authentication went wrong. Most probably the server didn’t accept the username/password combination provided.")    
    sys.exit(10)   
except smtplib.SMTPResponseException:
    print("Base class for all exceptions that include an SMTP error code. These exceptions are generated in some instances when the SMTP server returns an error code. The error code is stored in the smtp_code attribute of the error, and the smtp_error attribute is set to the error message.")
    sys.exit(11)   
except smtplib.SMTPException:
    print("Subclass of OSError that is the base exception class for all the other exceptions provided by this module.")
    sys.exit(12)
    
# NB reserved: sys.exit(13)
# NB reserved: sys.exit(14)

except ssl.SSLError:
    print("Error, raised to signal an error from the underlying SSL implementation.")
    sys.exit(15)
except ssl.SSLZeroReturnError:
    print("Error, raised when trying to read or write and the SSL connection has been closed cleanly. Note that this doesn’t mean that the underlying transport (read TCP) has been closed.")
    sys.exit(16)
except ssl.SSLWantReadError:
    print("Error, raised by a non-blocking SSL socket when trying to read or write data, but more data needs to be received on the underlying TCP transport before the request can be fulfilled.")
    sys.exit(17)
except ssl.SSLWantReadError:
    print("Error, raised by a non-blocking SSL socket when trying to read or write data, but more data needs to be received on the underlying TCP transport before the request can be fulfilled.")
    sys.exit(18)
except ssl.SSLWantWriteError:
    print("Error, raised by a non-blocking SSL socket when trying to read or write data, but more data needs to be sent on the underlying TCP transport before the request can be fulfilled.")
    sys.exit(19)
except ssl.SSLSyscallError:
    print("Error, raised when a system error was encountered while trying to fulfill an operation on a SSL socket. Unfortunately, there is no easy way to inspect the original errno number.")
    sys.exit(20)
except  ssl.SSLEOFError:
    print("Error, raised when the SSL connection has been terminated abruptly. Generally, you shouldn’t try to reuse the underlying transport when this error is encountered.")
    sys.exit(21)
except ssl.SSLCertVerificationError:
    print("Error, raised when certificate validation has failed. ")
    print('Verification error = ', getattr(ssl.SSLCertVerificationError, 'verify_message', 'unknown'))
    sys.exit(22);
    
except:
    # And finally, if you're still getting an SMTPAuthenticationError with an error code of 534, then you'll need to do yet another step for this to work.
    
    print ("not specified error; server reachable?; your email adress valid?; mail port (",smtp_port_local,") open?")
    sys.exit(13)
# NB reserved: print("Unable to open attachment ../python/format_101/temp/HPK_format_101.txt",)     
# NB reserved: sys.exit(14)   
else:
    print('mail sent successfully to: ', send_to) 
    sys.exit(0)



