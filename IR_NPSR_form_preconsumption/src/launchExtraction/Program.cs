using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;
using System.IO;

namespace LaunchExtraction
{
    class Program
    {
        private static TextWriterTraceListener RedirectOutput(string filepath)
        {
            //redirect output
            Trace.Listeners.Clear();
            TextWriterTraceListener twtl = new TextWriterTraceListener(filepath);
            twtl.Name = "TextLogger";
            twtl.TraceOutputOptions = TraceOptions.ThreadId | TraceOptions.DateTime;

            ConsoleTraceListener ctl = new ConsoleTraceListener(false);
            ctl.TraceOutputOptions = TraceOptions.DateTime;

            Trace.Listeners.Add(twtl);
            Trace.Listeners.Add(ctl);
            Trace.AutoFlush = true;
            Trace.WriteLine(String.Format("{0}\tSuccessfully redirected console output",DateTime.Now) );
            return twtl;
        }

        private static void GetParameterPaths(string filepath, string[]parameters)
        {
            int counter = 0;
            string line;
            // Read the file and display it line by line.
            var temp = File.Exists(filepath);
            try
            {
                System.IO.StreamReader file =new System.IO.StreamReader(filepath);
                while ((line = file.ReadLine()) != null)
                {
                    if (counter < parameters.Length)
                    {
                        parameters[counter] = line;
                    }
                    counter++;
                }

                file.Close();
            }
            catch (Exception e)
            {
                Trace.WriteLine(e.Message);

            }

        }

        static void Main(string[] args)
        {

            string parametersPath = @"C:\batchextractpaths\";  //this is where the file lives to get parameters
           
            const string consoleLogName=@"ConsoleLog.txt";
            const string parameterName = "parameterpaths.txt";
            if (args.Length > 0)
            {
                parametersPath = @args[0];
            }
            string consoleLogPath=String.Format("{0}{1}",parametersPath,consoleLogName);
            string[] extractionPaths = new string[4];
           

            TextWriterTraceListener listener = RedirectOutput(consoleLogPath);
           
           GetParameterPaths(String.Format("{0}{1}", parametersPath, parameterName),extractionPaths);
           string javaExecutable = extractionPaths[0] ;
           string jarFilePath = extractionPaths[1];
           string destFolder = extractionPaths[2];
           string schema = extractionPaths[3];
           string phase1 = "phase1.jar";
           string phase3 = "phase3.jar";
           string phase4 = "phase4.jar";
           string phase5 = "phase5.jar";


           Trace.WriteLine(String.Format("{1}\tJava Executable path: {0}", javaExecutable, DateTime.Now));
           Trace.WriteLine(String.Format("{1}\tJar File path: {0}", jarFilePath, DateTime.Now));
           Trace.WriteLine(String.Format("{1}\tDestination Folder: {0}", destFolder, DateTime.Now));
           Trace.WriteLine(String.Format("{1}\tschema path: {0}", schema, DateTime.Now));
           if (!File.Exists(javaExecutable))
           {

               Trace.WriteLine(String.Format("{1}\tJava Executable File does not Exist!: {0}", javaExecutable, DateTime.Now));
           }
           if (!Directory.Exists(jarFilePath))
           {

               Trace.WriteLine(String.Format("{1}\tJar File Directory does not exist: {0}", jarFilePath, DateTime.Now));
           }
           if (!Directory.Exists(destFolder))
           {

               Trace.WriteLine(String.Format("{1}\tDestination Folder does not exist!: {0}", destFolder, DateTime.Now));
           }
           if (!File.Exists(schema))
           {

               Trace.WriteLine(String.Format("{1}\tschema path does not exist!: {0}", schema, DateTime.Now));
           }


            ProcessStartInfo startInfo = new ProcessStartInfo();

            startInfo.FileName = javaExecutable;
            startInfo.CreateNoWindow = false;
            startInfo.WindowStyle = ProcessWindowStyle.Normal;
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardOutput = true;
           

            try
            {
                //phase1
                Trace.WriteLine("**Starting Phase 1***");
                string jarFile=String.Format("{0}\\{1}",jarFilePath,phase1);
                string arguments = String.Format(" -jar {0} {1}", jarFile, destFolder);
                startInfo.Arguments = arguments;
                  //Create the main process 
                Trace.WriteLine("Arguments: "+arguments);
                Process exeProcess = new Process();
                exeProcess.StartInfo = startInfo;
                exeProcess.Start();
                string output = exeProcess.StandardOutput.ReadToEnd();
                exeProcess.WaitForExit();
                Trace.WriteLine(output);
                
                //phase3
                Trace.WriteLine("**Starting Phase 3***");
                jarFile=String.Format("{0}\\{1}",jarFilePath,phase3);
                arguments = String.Format(" -jar {0} {1}", jarFile, destFolder);
                Trace.WriteLine("Arguments: " + arguments);
                startInfo.Arguments = arguments;
                exeProcess.StartInfo = startInfo;
                exeProcess.Start();
                output = exeProcess.StandardOutput.ReadToEnd();
                exeProcess.WaitForExit();
                Trace.WriteLine(output);

                  //phase4
                Trace.WriteLine("**Starting Phase 4***");
                jarFile=String.Format("{0}\\{1}",jarFilePath,phase4);
                arguments = String.Format(" -jar {0} {1} {2}", jarFile, destFolder,schema);
                Trace.WriteLine("Arguments: " + arguments);
                startInfo.Arguments = arguments;
                exeProcess.StartInfo = startInfo;
                exeProcess.Start();
                output = exeProcess.StandardOutput.ReadToEnd();
                exeProcess.WaitForExit();
                Trace.WriteLine(output);

                  //phase5
                Trace.WriteLine("**Starting Phase 5***");
                jarFile=String.Format("{0}\\{1}",jarFilePath,phase5);
                arguments = String.Format(" -jar {0} {1}", jarFile, destFolder);
                Trace.WriteLine("Arguments: " + arguments);
                startInfo.Arguments = arguments;
                exeProcess.StartInfo = startInfo;
                exeProcess.Start();
                output = exeProcess.StandardOutput.ReadToEnd();
                exeProcess.WaitForExit();
                Trace.WriteLine(output);
                

            }
            catch (Exception exception)
            {
                // Log error.
                Trace.WriteLine(String.Format("{1}\tError in main Process {0} ", exception.Message, DateTime.Now));
            }

           
        }
    }
}
