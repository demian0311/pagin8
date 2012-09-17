I have 2 employer provided computers.  One is a Mac Book Pro, the other is a Mac Pro.  I do my day to day development on the MBP because I want a smooth transition when I'm on the road.  I do this even though the desktop is a beast.  At home I use ShareMouse (a software KVM) so that the computers almost seem as one.  The desktop streams music from Pandora, has all the IM and email clients.

I still wanted to leverage the desktop for my workflow.  The setup I have allows me to run CI unit tests against my local (laptop) git repo.  I'll make changes, commit them and then the desktop will poll my laptop to see if there is anything new.  If there is something new then it does a full build and unit test.  I can continue doing other tasks on the laptop while the desktop is churning through our scala project (compiling scala code is resource intensive).  Then when I see that the build is in good shape I'll do a push to our team GitHub Enterprise Server.

### On your laptop
* Tell git it's okay to export your project by touching this file: `<path-to-project>/.git/git-daemon-export-ok `
* Serve up my local repo `git daemon --verbose --base-path=<path-to-project>`

### On your desktop
* Install Jenkins on the desktop
* Point desktop Jenkins to your git repo: `git://<your-network-ip>/.git`

The end result looks something like this:
![diagram](http://farm9.staticflickr.com/8320/7997187470_beec2aa6de_o.png)

<!--include:disqus.htm-->
