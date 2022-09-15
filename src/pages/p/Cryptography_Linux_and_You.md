Cryptography, Linux, and You
============================

Hal Canary

Talk given at [SWFLUG](http://swflug.org/), 2007-04-10

More information can be found at:
[https://halcanary.org/p/Cryptography\_Linux\_and\_You](/p/Cryptography_Linux_and_You)

Abstract:

> This 30 to 60 minute talk will deal with cryptography tools that commonly
> come with modern Linux distributions. The "You" part of the title refers to
> the fact that this will not be an overly technical talk and that these are
> tools you can use in your daily life and work. Some of these tools you are
> already using.
>
> Prerequisites: You know that Linux is an operating system and you know that
> many Linux users make use of the Command Line Interface to tell their
> computer what to do. You know that cryptography is the science and art of
> hiding a message so that only those who know a secret---called the key---can
> read it. You might even know a little about the the history of cryptography
> before about 1945, but we won't touch on ancient cryptography at all.

Optional Activities:

> If several attendees bring laptops running Linux (or other flavor of Unix),
> we could exchange GPG-encrypted emails as a demonstration. While we are at it
> we could do an old-fashioned key-signing party.

After the talk is given, I'll post the slides and the text here on this page.

* * *

[Here are the slides from the talk.](/pub/2007-04-10-CLY/cryptography_linux_and_you_slides.pdf)

Here is the prepared text:

<script type="text/javascript">
const slide = (x) => {
    for (var i = 1; i <= 29; i++) {
        document.getElementById("slide-" + i).style.display = (x === i) ? "inline" : "none";
    }
    return true;
};
window.addEventListener("load", () => { slide(1); });
</script>

<div id="slide-1"><img src="/images/ev0001.png" alt="[slide]"></div>
<div><button onclick="slide(1)">SLIDE 1</button></div>

The title of this talk is "Cryptography, Linux, and You". We will be dealing
with cryptography tools that commonly come with modern Linux distributions. The
"You" part of the title refers to (1) the fact that this will not be an overly
technical talk. It also refers to (2) the fact that these are tools you can use
in your daily life and work. Some of these tools you are already using.

Assumptions: You know that Linux is an operating system and you know that many
Linux users make use of the Command Line Interface to tell their computer what
to do. You know that cryptography is the science and art of hiding a message so
that only those who know a secret---called the key---can read it.

You know about the the history of cryptography: scytales (skitalys), the Caesar
cypher, Cryptoquips, Enigma machines and Bletchley Park, Shannon security, et
cetera. Because I won't touch on any of this history.

<div id="slide-2"><img src="/images/ev0002.png" alt="[slide]"></div>
<div><button onclick="slide(2)">SLIDE 2</button></div>

We are going to talk about three broad topics: hash functions, symmetric
cryptography, and asymmetric encryption. Strictly speaking, cryptographic hash
functions are not a form of cryptography, since cryptography literally means as
"secret writing". On the other hand, cryptographic hash functions are
incredibly useful for cryptographic applications.

Another thing we are going to touch on is digital signatures. These are not
"secret writing" either, but but they are (1) useful for securing encrypted
channels of communication, (2) make use of algorithms related to cryptography,
and (3) useful in themselves.

<div id="slide-3"><img src="/images/ev0003.png" alt="[slide]"></div>
<div><button onclick="slide(3)">SLIDE 3</button></div>

A checksum is a mathematical function that can be used to verify that an input
has not been accidentally changed.

For example, the 10 digit ISBN (International Standard Book Number) that every
book is identified with includes a checksum in the last digit.

CHECK\_DIGIT = 11 - (∑(i=1, 9, (11 - i) · d\[i\]) modulo 11)

Hash functions are similar to checksums. For the sake of this talk, they are
the same thing. \*Cryptographic\* Hash Functions have the additional property
that it is relatively hard to make a change in the input to produce the same
output. Because of the danger of a brute force attack, useful cryptographic
hash functions have a large but finite range, larger than 2^128 elements.
						
Here's a list of four common cryptographic hash functions---md5, sha1, and sha2
(sha256 and sha512)---and the size of their range. Md5 and sha1 are broken,
which means that there is an attack on them that is faster than a brute force
attack. These attacks still take some processing power, so they are only "kind
of" broken. Any new application should be designed to use the sha2 family of
functions.

The NSA and NIST have announced that they plan on creating a new family of
cryptographic hash functions to replace sha2. It may be some years before this
happens.

<div id="slide-4"><img src="/images/ev0004.png" alt="[slide]"></div>
<div><button onclick="slide(4)">SLIDE 4</button></div>

Here's an example of how a you might make use of a hash function. These
functions take in a stream of bits---for example a file on a computer--- and
output a N-bit binary number. That number is usually represented as a
hexadecimal number. In this example, sha1 gives 40 digit hexadecimal number.
(Or more precisely a 40 hexadigit number) A hexadecimal digit is base 16 - 16
is 2^4, so each digit is 4 bits (half a byte). 4 bits per hexadigit times 40
digits gives 160 bits, which is what we expected form the information that I
gave you before!

Here I create two files test1 and test2. These two files differ by only one
character---a single period.

Aside: the echo command outputs the string "hello world". The ">" symbol
redirects the output of echo into a file, in this case into the file test1

When I use the sha1sum program and tell it to give me a checksum on these two
files, it outputs two checksums, one for each file. Notice how different the
two sums are, even if the inputs were very much alike!

So I don't have to remember these checksums, I can store them in a file, which
I'll call "SHA1SUM.txt". Later, I'm going to come along and check to see that
these two files haven't accidentally changed. to do that I'll use "sha1sum -c".
The "-c" means check and reads input in a form identical to the output of
sha1sum. With "-c" I don't have to manually check the sums with my own
eyeballs.

<div id="slide-5"><img src="/images/ev0005.png" alt="[slide]"></div>
<div><button onclick="slide(5)">SLIDE 5</button></div>

Here I use some fancy commands to add a newline character to the end of the
file test2. Now it should give a different checksum because the file has
changed This time, "sha1sum -c" gave an error message!

<div id="slide-6"><img src="/images/ev0006.png" alt="[slide]"></div>
<div><button onclick="slide(6)">SLIDE 6</button></div>

Now we finally get to some real encryption!

<div id="slide-7"><img src="/images/ev0007.png" alt="[slide]"></div>
<div><button onclick="slide(7)">SLIDE 7</button></div>

For this example I want to encrypt these two files test1 and test2. The easiest
way to do that is to put them in a new directory by themselves than archive the
directory with the "tar -cz" command. This creates the compressed Tar archive
secretstuff.tgz. I then encrypt it with a program called GPG. I'm going to use
GPG in "symmetric mode" so I use the command "gpg -c" Here "-c" means
"symmetric". I think "-s" was already taken.

It asks for a passphrase twice---to make sure I entered it correctly.

This creates a file called secretstuff.tgz.gpg

<div id="slide-8"><img src="/images/ev0008.png" alt="[slide]"></div>
<div><button onclick="slide(8)">SLIDE 8</button></div>

To be secure, I'm going to use the shred program to securely delete the
original archive file. To recover the original file, I simply run the command
"gpg secretstuff.tgz.gpg". It asks for the passphrase and if I enter it
correctly, it will spit out the original file.

<div id="slide-9"><img src="/images/ev0009.png" alt="[slide]"></div>
<div><button onclick="slide(9)">SLIDE 9</button></div>

Why did I use the term "passphrase" and not "password". There is a subtle
difference. Passwords are short, while passphrases can be very long. If I knew
that your password was only eight characters long, I could try every eight
combination of eight letters - that would only take me a few days if I could
try one every microsecond. I could go much faster if your password is subject
to a dictionary attack. Say you are trying to attack my home server with a
brute-force attack against my ssh daemon. My sshd always closes the connection
after N incorrect passwords and waits about a second before it asks for another
password.

So passwords are great for situations where the system can control the number
of tries that can be allowed.

If an attacker gets a hold of the file secretstuff.tgz.gpg, that attacker could
try to crack that file as many times a second as he wants to. So you need to
pick a much harder to guess passphrase. Here's a table that lists the relative
"hardness to guess" of several passphrases. On this scale, a 1.0 is
approximately equals the security of a 128 bit number. If you guessed one
number every femptosecond (10^-15) it would take almost a million times the age
of the universe before you've exhausted all the 128 bit numbers out there. So
2^128 is considered a big number. On the other hand, cryptosystems with a key
64 bits long have been broken with brute force attacks (584542 per microsecond
for a year).

<div id="slide-10"><img src="/images/ev0010.png" alt="[slide]"></div>
<div><button onclick="slide(10)">SLIDE 10</button></div>

If I want a 128 bit random number, that's 16 bytes. Let's grab 16 byes out of
/dev/random --- Linux's random device. We'll use the head program to grab the
first 16 bytes it gives us. Then we'll that that and pipe it into the the
hexdump program so that we can get a human-readable format.
de4226f80c92e9de1030f4811b8b9a07---that's a good passphrase, but you'll never
remember it. We could also use the base64 program to make a shorter
human-readable passphrase.

There are a bunch of different ways to come up with perfectly random passwords.
Diceware is software that uses the random throw of dice to produce a
passphrase.

I choose to trust Linux's Random Device. The Linux Kernel watches its
environment---keyboard stokes, mouse movements, Ethernet traffic, et
cetera---and gathers randomness form the low bits of of the time when such
interrupts are received. Such inputs are considered very random. Since the
Linux kernel keeps track of how much randomness it has, /dev/random is
considered a true random number generator, and not a pseudorandom number
generator.

Microsoft's function CryptGenRandom() is not considered as secure as Linux's
/dev/random.

Just to see how /dev/random collects randomness from keystrokes, here's a
little program I wrote, timeinmicroseconds, that prints out the current time to
microsecond resolution. If I run it like this:

    while read -s -n 1 x ; do timeinmicroseconds ; done

it will print out the time on every keystroke, until I hit Ctrl-C. Notice that
the current time in seconds is not very random, but the microseconds part of
the number is very random looking. Now you will think twice before doing this:

	srand(time(0));
	int num = rand();

which only looks at the current time in seconds. Here's a much better way of
getting a random number in C:

	FILE \*devrandom;
	int num;
	devrandom = fopen("/dev/random", "rb");
	fread(&num, sizeof(num), 1, devrandom);
	close(devrandom);

<div id="slide-11"><img src="/images/ev0011.png" alt="[slide]"></div>
<div><button onclick="slide(11)">SLIDE 11</button></div>

Aespipe is a simple program that does one thing and does it well---it
implements the AES (Advanced Encryption Standard) block cypher. Aespipe can be
found in the Ubuntu universe or can be downloaded form this URL.

Aespipe is about twice as fast as GPG for symmetric encryption.

Aespipe either needs one 128-bit key or (in multi-key mode) 65 different
128-bit keys. It always gets these keys by applying a cryptographic hash
function to your passphrase(s). You can give it a passphrase in three different
ways. (1) You can type it in at the prompt (2) you can leave it in a file and
use the "-P file" option to read that file in (3) you can gpg-encrypt a file
and use the "-K" option to access that file.

<div id="slide-12"><img src="/images/ev0012.png" alt="[slide]"></div>
<div><button onclick="slide(12)">SLIDE 12</button></div>

We first create an excessively long passphrase (more entropy than we will need,
but why not?) and leave it in a ﬁle pass.txt

In the second step, we will use the pipeline to take the output of tar and
input it into aespipe. We redirect the final output into the file
secretstuff.tgz.aes.

In the next sequence, we add a passphrase to secure the keyfile!

When decrypting, use the "-d" option!

I recently backed up 58 GB of data through aespipe and onto an external drive.
The bottleneck seemed to be the aespipe program, but I was able to write about
5MB of data a second.

<div id="slide-13"><img src="/images/ev0013.png" alt="[slide]"></div>
<div><button onclick="slide(13)">SLIDE 13</button></div>

Public Key, or Asymmetric, Cryptography is relatively new. It was first made
public in the 1970s but was in use by governments for some years before that.
Before public-key cryptography, if N people want to communicate secretly among
themselves, they would need N\*(N-1)/2 separate keys. Without Public Key
Crypto, Internet commerce would never have happened, among other things.

<div id="slide-14"><img src="/images/ev0014.png" alt="[slide]"></div>
<div><button onclick="slide(14)">SLIDE 14</button></div>

Public-key cryptography (PKC) uses different keys to encrypt and decrypt your
message! These two keys are mathematically related and must be generated at the
same time.

The public key is used to encrypt the message, while the private key is used to
decrypt it.

In 1991, Philip Zimmerman wrote PGP (pretty good privacy) and brought public
key crypto to the masses for the first time.

The algorithms that PGP make use of are not mathematically proven to be
unbreakable. At best, you can still bruteforce calculate the private key from
the public key, given an infinite amount of computing power or a quantum
computer with a few thousand qbits.

OpenPGP is a standard for PKC. GPG (Gnu Privacy Guard) is a F/OSS
implementation of the OpenPGP standard and is included in most distros. After
you have generated a key pair, you will want to publish the public key and keep
the private key safe. By default, GPG encrypts your private key with a
passphrase as an added layer of security in case some one gains access to your
computer.

In actuality, GPG or PGP encrypts your message with a symmetric cypher and then
encrypts the key to that cypher with PKE. This is considered faster and safer.

<div id="slide-15"><img src="/images/ev0015.png" alt="[slide]"></div>
<div><button onclick="slide(15)">SLIDE 15</button></div>

Let's start by generating a new keypairs. If you forget the command-line
options to GPG, use the command "man gpg" to get the full documentation. the
"--gen-key" option lets you create a new keypair.

Let's take the default options here. The key is going to have a copy of your
name and email address for easy searching online. When it's done reading from
the random device, it generates the key and drops the key in the ~/.gnupg/
directory. Each key has an ID---an 8 digit hexadecimal number, and a
fingerprint, a 40 digit hexadecimal number. The fingerprint is just a
cryptographic hash of the public key and can be used to verify that two copies
of a key are the same key. This is used to prevent a man-in-the middle attack.

You can get the fingerprint later by "gpg --fingerprint".

There are two ways of getting someone's key. They can give you a keyfile they
create with "gpg -a --export KEYID > KEYID.txt". You could import it with "gpg
--import KEYID.txt".

Another option is to search a keyserver for a key, either over the web or using
GPG's "gpg --search-keys 'real name'" or "gpg --recv-keys KEYID". A key can be
uploaded via the web or "gpg --send-keys KEYID".

Once I've generated my own key pair and acquired and verified someone else's
public key, I can encrypt a file to send to them with "gpg --sign --encrypt
--recipient 'NAME' FILE".

If you receive a file, you can usually process it with "gpg FILE".

Ask for volunteers to send some files back and forth.

<div id="slide-16"><img src="/images/ev0016.png" alt="[slide]"></div>
<div><button onclick="slide(16)">SLIDE 16</button></div>

Digital Signatures are kind of like the opposite of Public Key Encryption. They
are used to verify that the signed file has not changed since they were signed
by someone who possessed the private key. Strictly speaking, you sign the
digest from a cryptographic hash function and not the file itself, but as long
as the hash function is secure, this is practically equivalent and much faster.

About ten years ago, a law was passed here in the United States which made
Digital Signatures legally equivalent to ordinary signatures. This law was
intended to facilitate Internet commerce.

The most well know algorithm for digital signatures is DSA (Digital Signature
Algorithm). I will show five applications of Digital Signatures.

<div id="slide-17"><img src="/images/ev0017.png" alt="[slide]"></div>
<div><button onclick="slide(17)">SLIDE 17</button></div>

The first application is signing a plain text document. This is used a lot for
a plain file on posted on a newsgroup or a web page where it is not feasible to
attach a second file with a signature file. Before email clients learned to
recognize attached signatures, this format was often used to sign emailed
messages as well.

Let's go through an example of signing something and verifying it. The commands
are "gpg --clearsign" and "gpg --verify".

<div id="slide-18"><img src="/images/ev0018.png" alt="[slide]"></div>
<div><button onclick="slide(18)">SLIDE 18</button></div>

The next way that this is often done is to clearsign a SHA1SUM file. This
became popular in situations where a person was already producing SHA1SUM files
and it was trivial to add a "gpg --clearsign" step to their routine. Then users
who were accustomed to checking files with "sha1sum -c" can continue with no
change to their routines and users who want to "gpg --verify" as well can do
that for added security.

Power users will insist on combining the two processes with a pipeline.

<div id="slide-19"><img src="/images/ev0019.png" alt="[slide]"></div>
<div><button onclick="slide(19)">SLIDE 19</button></div>

Here's a real-life example: Red Hat already was providing checksums for
downloaded files. It was trivial for them to begin signing those checksums.

<div id="slide-20"><img src="/images/ev0020.png" alt="[slide]"></div>
<div><button onclick="slide(20)">SLIDE 20</button></div>

Signing a random binary file. The "gpg --detach-sign" command creates a
separate signature. Your email client probably does a similar thing when it
GPG-signs an email and sends the signature as an attachment.

<div id="slide-21"><img src="/images/ev0021.png" alt="[slide]"></div>
<div><button onclick="slide(21)">SLIDE 21</button></div>

For example, if I use Evolution or Thunderbird, both popular GUI email clients
for Linux, then it is easy to enable encryption and digital signatures. In
Evolution, you can get to this screen from the Account Editor from Preferences.
Just enter the KEYID of the GPG key you want to use to sign mail and it just
works. You will have to provide your passphrase to unlock the private key each
time you use it.

Signed email should be ignored by clients unable to make use of those
signatures; it will serve as notice to everyone else that you use GPG!

<div id="slide-22"><img src="/images/ev0022.png" alt="[slide]"></div>
<div><button onclick="slide(22)">SLIDE 22</button></div>

Secure Sockets Layer (SSL) or TLS (Transport Layer Security) is a layer of
encryption that can be applied to any TCP/IP connection. When it is applied to
HTTP (the web's transfer protocol) the result is HTTPS, usually denoted by the
https:// URL scheme and the little lock icon in your browser window. SSL makes
use of PKE like something this: (I may be glossing over details)

1) Client (Web Browser) opens \*clear\* connection to server (Web Server).

2) Server responds by sending its public key.

3) Client looks at the key and sees that it has been digitally signed by a
Certificate Authority.

4) If the client ALREADY has a public key for that Certificate Authority, it
checks the signature. If not valid, it throws an error message.

5) If everything is on the up-and-up, the client generates a random session key
and asymmetrically encrypts it with the server's key. It then responds to the
server with this encrypted message.

6) The server uses its private key to decrypt the session key. From here on
out, both the client and the server will encrypt everything they send each
other with this session key.

Before any of this can happen, the following must happen behind the scene:

A) The server must generate a keypair.

B) The server must give a CA money and prove to the CA that they are who they
say they are in exchange for a signature on the public key.

C) That CA must convince the makers of all web browsers to include the CA's
public key with the web browser.

<div id="slide-23"><img src="/images/ev0023.png" alt="[slide]"></div>
<div><button onclick="slide(23)">SLIDE 23</button></div>

You can go to this page in Mozilla Firefox and get a list of all the CA's that
it trusts.

<div id="slide-24"><img src="/images/ev0024.png" alt="[slide]"></div>
<div><button onclick="slide(24)">SLIDE 24</button></div>

Here's a screenshot from the "Page Info" box when I visit my bank's website.
Bank of America paid VeriSign to sign its key.

Why go through all this rigmarole? To prevent a Man-in-the-Middle attack. If a
malicious person intercepted my original connection to the bank and gave me a
his own key, he could turn around and echo everything I said to the bank using
the bank's own key.

Draw a picture.

<div id="slide-25"><img src="/images/ev0025.png" alt="[slide]"></div>
<div><button onclick="slide(25)">SLIDE 25</button></div>

SSH (Secure Shell) was originally designed to replace rsh and telnet, programs
which allowed remote logins to servers \*without\* any encryption. Even the
passwords were sent in cleartext. On an ethernet, it was ridiculously easy to
use a program called Ethereal to snoop on telnet sessions and steal passwords.

The only two versions of SSH I use these days are OpenSSH, which comes with
most Linux distributions, and Putty, which is available in a native Windows
version.

Like SSL, the session key for your SSH connection is encrypted with the
server's public key. When installing the SSH daemon, you generate a keypair.

In order to prevent a man-in-the-middle attack, you'll need to manually check
the fingerprint on the server's key. You'll only need to do this the first time
your client connects to a server or if a server gets a new keypair.

To find the fingerprints on a server, you can use the command "ssh-keygen -lf".
The public keys are kept in the files /etc/ssh/ssh\_host\_\*key.pub

First and foremost, SSH is used to remotely log into a command-line environment
on a remote machine. For those who do a lot of work on the command line
already, the utility of this is obvious. For example, I have used remote logins
to:

1.  log into a web server and make changes to the files there directly---without
    the need to make local copies and ftp them over to the server.

2.  Log into a Linux machine that I was using as a router so I can modify the
    firewall rules using iptables.

3.  Remotely long into a workstation to run mathematical or physical simulations
    that might take a lot of computational power and time to run.

4.  Log into a email server and run Mutt or Pine to locally read and respond to
    my email.

5.  Use "wall" to talk to other users on the same machine.

But that's not all!

<div id="slide-26"><img src="/images/ev0026.png" alt="[slide]"></div>
<div><button onclick="slide(26)">SLIDE 26</button></div>

SSH can be used to copy files or even whole directories with the "scp" or
"sftp" commands. I am especially fond of the scp command, which has beautiful
syntax, stolen from the old "rcp" command. In my opinion, scp has more of the
Unix Philosophy than sftp does.

In the rsync example, we use the rsync command to manage the actual copying of
files, but we \*connect\* to the ssh daemon and don't need to run a rsync
daemon. Rsync is smart enough to minimize network usage at the expense of
processor time.

You can also run a rsync daemon on the other end, without the need for ssh.
This is often used for anonymous, download-only rsync.

Port forwarding. In the first example, we forward the X11 protocol. Now
programs that run on the remote machine running a ssh server can act as X11
clients for the X11 server that runs on the local machine running a ssh client.
If you've got Linux on both ends, you probably already have a X11 server on
your local machine---it's what your GUI (graphical user interface) is built on!

Example: run xclock or xterm.

Dynamic Port forwarding. In the second example, we dynamically forward
localhost port 12345 to be a SOCKS port. To make use of it, we'll have to
configure an application to make use of this port. But first let's draw a
picture:

<div id="slide-27"><img src="/images/ev0027.png" alt="[slide]"></div>
<div><button onclick="slide(27)">SLIDE 27</button></div>

Here is a graphic which shows the relevant parts of a hypothetical situation I
find myself in all the time. I'm at a coffee shop and have my laptop with me. I
want to browse the web off the free WiFi someone has provided, but since it is
not encrypted, anyone could listen in. Worse, I know the hotspot provider could
be monitoring my traffic. Even if I use SSL, which is not always possible,
he'll know what web sites I visit. Strictly speaking, you always have this sort
of problem with any connection to the Internet, but with your ISP, you pay them
money to give you Internet service. It is in their best intre$t to respect your
privacy.

Beforehand, I have

1.  Figured out how to open port 22 (or other port) on my DSL or cable modem and
    how to forward that port to my Linux workstation on my home network.

2.  Run sshd on my workstation, opened port 22 on my iptables firewall, and left
    my computer running while I'm out.

3.  Gone to dyndns.org and set up a free account so example.dyndns.org now
    points at my home machine. I run the ddclient daemon that periodically updates
    the dyndns dns server on my current DHCPed IP address.

4.  Have a copy of my home machine's SSH key fingerprint either with me printed
    on a piece of paper, on a computer file, or in my laptop's ~/.ssh/known\_hosts
    file.

Now all I need to do is "ssh -D 12345 example.dyndns.org". The hypothetical
malevolent ISP \*will\* know that you have opened a ssh connection to
example.dyndns.org, and will see a bunch of encrypted information going back
and forth, but if you trust the SSH encryption, then there is no way for him to
know anything beyond how much information is being passed in each direction.
(Remember that I will completely fail to mention any sort of \*deniable\*
cryptography today.)

We will want to convince my web browser to send any outgoing packets through
the ssh tunnel to my workstation, then out to the Internet at large,
unencrypted.

<div id="slide-28"><img src="/images/ev0028.png" alt="[slide]"></div>
<div><button onclick="slide(28)">SLIDE 28</button></div>

I need to tell my web browser to use the SOCKS proxy at localhost:12345. Here's
how I do it in Firefox 2.0.0.x. I use the "MM3 ProxySwitch" Add-On to quickly
switch back and forth between proxied and unproxied modes. My configuration
file looks this:

	\[socksproxy
	  socks=127.0.0.1:12345
	  noProxy=127.0.0.1
	\]

And that's all there is. I'd like to bust out Ethereal to show how http traffic
is encrypted now, but that's out of the scope of this talk.

<div id="slide-29"><img src="/images/ev0029.png" alt="[slide]"></div>
<div><button onclick="slide(29)">SLIDE 29</button></div>

Conclusions: There's a lot more to this subject than just picking the right algorithm!

* * *

<div class="rightside">

<div class="rightside"><em>file modification time: 2007-04-11 18:22:13</em></div>

</div>
