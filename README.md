# SWT-Widgets


This repository collects some of my favourite SWT widgets. 

Some of those widgets are ported from other open source projects, others are written by myself. The reasons why I port codes from other open source projects instead of using them directly are: 

1. I don't want my project depends on too many other projects, since it's hard to mange a lot of dependencies.
2. Sometimes I have to modify the widgets to satisfy my own requirements.

## Dependent Projects

This project depends on the following projects. The depended projects **must** be built in the order listed below.

* [pom-root](https://github.com/Haixing-Hu/pom-root)
* [Java-Commons](https://github.com/Haixing-Hu/commons)

## Widgets

### StarRating

This is a modification of the StarRating widget of the [OPAL][opal] project. It implements a simple star rating toolbar that allows the user to rate anything. 

There is a difference between this implementation and the [OPAL][opal] implementaiton: when the user click a selected star, 

- if the star has not been selected, the star and all stars below it will be selected;
- if the star is the highest star that has been selected, the star will be unselect by the clicking; 
- if the star is not the highest star that has been selected, the clicking will cause the star and all stars below it be selected and all stars above it be unselected. 

In this way, we could easily unselect all stars. 

#### Screen Shot

![Screen Shot of the StarRating Widget](https://raw.githubusercontent.com/Haixing-Hu/swt-widgets/master/screenshots/StarRatingExample.png)

#### Usage

In order to create a StarRating component, you must provide 

- the parent component, 
- the SWT styles, 
- the size of the star (`StarRating.Size.SMALL` or `StarRating.Size.BIG`), and 
- the maximum number of stars (i.e., the maximum rating level).

	StarRating rating = new StarRating(shell, SWT.NONE, StarRating.Size.SMALL, 10);

Or you can use the default maximum number of starts (which is 5):

	StarRating rating = new StarRating(shell, SWT.NONE, StarRating.Size.BIG);
		 
#### Example

An example is located in the source repository:

	src/test/java/com/github/haixing_hu/swt/starrating/StarRatingExample.java


###  BreadCrumb

This is the BreadCrumb toolbar comes from the [OPAL][opal] project.

#### Screen Shot

![Screen Shot of the BreadCrumb Toolbar](https://raw.githubusercontent.com/Haixing-Hu/swt-widgets/master/screenshots/BreadCrumbExample.png)

##### Usage

Example usage:

	final BreadCrumb bc = new BreadCrumb(shell, SWT.BORDER);
	
	final BreadCrumbItem labelItem = new BreadCrumbItem(bc, SWT.CENTER|SWT.NONE);
	labelItem.setText("Label");
	
	final BreadCrumbItem buttonItem = new BreadCrumbItem(bc, SWT.CENTER|SWT.PUSH);
	buttonItem.setText("Button");
	
	final BreadCrumbItem toggleItem = new BreadCrumbItem(bc, SWT.CENTER|SWT.TOGGLE);
	toggleItem.setText("Toggle");
	toggleItem.setImage(toggleImage);
	toggleItem.setSelectionImage(toggleSelectionImage);


##### Example

An example is located in the source repository:

	src/test/java/com/github/haixing_hu/swt/breadcrumb/BreadCrumbExample.java


## References

1. OPAL: [https://code.google.com/a/eclipselabs.org/p/opal/][opal]


[opal]: https://code.google.com/a/eclipselabs.org/p/opal/
