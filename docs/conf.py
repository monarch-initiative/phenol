# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Project information -----------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#project-information

project = 'Phenol'
copyright = '2023, Peter N. Robinson, Sebastian Koehler, Manuel Holtgrewe, Sebastian Bauer, Julius OB Jacobsen, HyeongSik Kim, Michael Gargano, Daniel Danis'
author = 'Peter N. Robinson, Sebastian Koehler, Manuel Holtgrewe, Sebastian Bauer, Julius OB Jacobsen, HyeongSik Kim, Michael Gargano, Daniel Danis'
# The short X.Y version.
version = '2.0'
# The full version, including alpha/beta/rc tags.
release = '2.0.3'

# -- General configuration ---------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#general-configuration

extensions = []

templates_path = ['_templates']
exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']
source_suffix = ['.rst', '.md']



# -- Options for HTML output -------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#options-for-html-output

html_theme = 'sphinx_rtd_theme'
html_static_path = ['_static']
html_css_files = ['phenol.css']
