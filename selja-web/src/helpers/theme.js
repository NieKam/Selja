import styled from 'styled-components'
import { red } from '@material-ui/core/colors';
import { createMuiTheme } from '@material-ui/core/styles';
import { Typography } from '@material-ui/core';

export const StyledA = styled.a`
  color: white;
  text-decoration: none;
  margin-left: 5px;
  &:hover {
    color: grey;
  }
`
export const theme = createMuiTheme({
    palette: {
        type: "dark"
    }
})

export const AdItemStyle = styled.div`
    border-radius: 10px;
    padding: 14px;
    margin-bottom: 14px;
    background: #2d2e30;
    border: 1px solid #5e6366;
    max-width: 70%;
`

export const StyledNameText = styled.div`
    font-size: 21px;
    margin-left: 28px;
    padding: 4px;
`

export const StyledPriceText = styled.div`
    color: #fff;
    font-size: 24px;
    margin-left: 28px;
    padding: 4px;
`

export const StyledDistanceText = styled.p`
    font-size: 18px;
`

export const StyledError = styled.p`
    color: rgb(219, 81, 31);
    text-align: center;
`

export const StyledTimeText = styled.div`
    font-size: 18px;
    color: ${props => props.color};
`

export const StyledHeaderText = styled.h1`
  color: #fff;
`

export const TypographyTitleStyle = styled(Typography)`
    padding : 18px;
`
export const TypographyFooterStyle = styled(Typography)`
    padding : 18px;
    text-align: center;
`

export default theme;
