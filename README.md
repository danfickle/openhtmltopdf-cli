# openhtmltopdf-cli

This is a CLI for converting HTML files to PDF documents.
Under the hood it uses the [openhtmltopdf](https://github.com/danfickle/openhtmltopdf) library.
If you'd like to try it out online before downloading, please see the [sandbox](https://sandbox.openhtmltopdf.com/?file=invoice.htm).

## Requirements

You will need Java (JRE or JDK) installed on your machine (version 8 or above). You can download a version for your platform at [Azul](https://www.azul.com/downloads/).

## Download

Please visit the [releases](https://github.com/danfickle/openhtmltopdf-cli/releases) page.

## Usage

````
Usage: java -jar openhtmltopdf-cli-1.0.10-1.jar convert [-bqvx] -i=<input> -o=<output>
Converts a single html file into a PDF
  -b, --block             Block linked resources (CSS, images, fonts)
  -i, --input=<input>     The html input file
  -o, --output=<output>   The PDF output file
  -q, --quiet             Quiet logging
  -v, --verbose           Verbose logging
  -x, --xhtml             Use to specify that the input file is valid XHTML
                            (skipping the HTML to XHTML step)
````

## Example

````bash
java -jar openhtmltopdf-cli-1.0.10-1.jar convert -v -i doc.html -o doc.pdf
````

## Sample HTML file

````html
<html>
<head>
<style>
@page {
  @top-right {
    content: "Page " counter(page) " of " counter(pages);
  }
}
.greeting {
  font-size: 25px;
  text-align: center;
  color: orange;
}
</style>
</head>
<body>

  <h1 class="greeting">
    Hello World!
  </h1>

</body>
</html>
````


